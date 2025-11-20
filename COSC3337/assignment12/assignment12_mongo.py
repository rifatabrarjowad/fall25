from pymongo import MongoClient
from typing import List, Dict, Any
import os
import sys

def get_database(db_name: str = "shop_assignment12"):
    connection_string = "http://localhost:8001"
    client = MongoClient(connection_string)
    return client[db_name]

def infer_delimiter(path: str) -> str:
    ext = os.path.splitext(path)[1].lower()
    if ext == ".tbl":
        return "|"
    return ","


def parse_tabular_file(path: str) -> List[Dict[str, Any]]:
    delimiter = infer_delimiter(path)
    docs: List[Dict[str, Any]] = []
    with open(path, "r", encoding="utf-8", errors="ignore") as f:
        header_line = f.readline()
        if not header_line:
            return docs
        header_line = header_line.rstrip("\n")
        raw_headers = header_line.split(delimiter)
        headers = [h.strip() for h in raw_headers if h.strip() != ""]

        for line in f:
            line = line.rstrip("\n")
            if not line:
                continue

            parts = line.split(delimiter)
            while len(parts) > 0 and parts[-1].strip() == "":
                parts.pop()

            if len(parts) < len(headers):
                parts += [""] * (len(headers) - len(parts))
            if len(parts) > len(headers):
                parts = parts[:len(headers)]
            row: Dict[str, Any] = {}
            for name, value in zip(headers, parts):
                value = value.strip()
                if value == "":
                    casted: Any = None
                else:
                    # Try int
                    try:
                        casted = int(value)
                    except ValueError:
                        # Try float
                        try:
                            casted = float(value)
                        except ValueError:
                            casted = value  # keep string

                row[name] = casted

            docs.append(row)

    return docs

def load_customers_and_orders(
    db,
    customer_path: str = "customer.tbl",
    orders_path: str = "orders.tbl",
) -> None:
    customers_coll = db["customers"]
    orders_coll = db["orders"]

    print("[*] Dropping collections 'customers' and 'orders' (if they exist)...")
    customers_coll.drop()
    orders_coll.drop()

    print(f"[*] Reading customers from '{customer_path}'...")
    customer_docs = parse_tabular_file(customer_path)
    print(f"    Parsed {len(customer_docs)} customer records")

    print(f"[*] Reading orders from '{orders_path}'...")
    order_docs = parse_tabular_file(orders_path)
    print(f"    Parsed {len(order_docs)} order records")

    if customer_docs:
        print("[*] Inserting customers into MongoDB...")
        customers_coll.insert_many(customer_docs)
    else:
        print("[!] No customer records parsed.")

    if order_docs:
        print("[*] Inserting orders into MongoDB...")
        orders_coll.insert_many(order_docs)
    else:
        print("[!] No order records parsed.")

    print("[*] Task 1.1 completed (data loaded).\n")

def list_customers_with_urgent_orders(db) -> None:
    """
    Task 1.2:
      Query the MongoDB and print a list of customers who have orders
      with ORDER-PRIORITY = "1-URGENT".
    """
    customers_coll = db["customers"]
    orders_coll = db["orders"]

    print("[*] Task 1.2 – Customers with ORDER-PRIORITY = '1-URGENT'")
    urgent_custkeys = orders_coll.distinct(
        "CUSTKEY",
        {"ORDER-PRIORITY": "1-URGENT"}
    )

    if not urgent_custkeys:
        print("    No customers found with 1-URGENT orders.\n")
        return
    cursor = customers_coll.find(
        {"CUSTKEY": {"$in": urgent_custkeys}},
        {"_id": 0, "CUSTKEY": 1, "NAME": 1, "PHONE": 1, "MKTSEGMENT": 1}
    ).sort("CUSTKEY", 1)

    for doc in cursor:
        print(
            f"    CUSTKEY={doc.get('CUSTKEY')}  "
            f"NAME={doc.get('NAME')}  "
            f"PHONE={doc.get('PHONE')}  "
            f"MKTSEGMENT={doc.get('MKTSEGMENT')}"
        )
    print()


def top_customers_by_total_price(db, top_n: int = 10) -> None:
    customers_coll = db["customers"]
    orders_coll = db["orders"]
    print(f"[*] Task 1.3 – Top {top_n} customers by total order price")

    pipeline = [
        {
            "$group": {
                "_id": "$CUSTKEY",
                "total_spent": {"$sum": "$TOTALPRICE"},
                "order_count": {"$sum": 1}
            }
        },
        {"$sort": {"total_spent": -1}},
        {"$limit": top_n},
        {
            "$lookup": {
                "from": "customers",
                "localField": "_id",          # CUSTKEY
                "foreignField": "CUSTKEY",    # in customers collection
                "as": "customer"
            }
        },
        {"$unwind": "$customer"}
    ]

    results = list(orders_coll.aggregate(pipeline))

    if not results:
        print("    No orders found.\n")
        return

    rank = 1
    for doc in results:
        cust = doc.get("customer", {})
        print(
            f"    #{rank:02d}  CUSTKEY={doc['_id']}  "
            f"NAME={cust.get('NAME')}  "
            f"TOTAL_SPENT={doc['total_spent']:.2f}  "
            f"ORDER_COUNT={doc['order_count']}"
        )
        rank += 1

    print()

def main(argv: List[str]) -> None:
    # default file names
    customer_path = "customer.tbl"
    orders_path = "orders.tbl"

    if len(argv) >= 2:
        customer_path = argv[1]
    if len(argv) >= 3:
        orders_path = argv[2]

    print("[*] Connecting to MongoDB on localhost...")
    db = get_database()

    # Task 1.1
    load_customers_and_orders(db, customer_path, orders_path)

    # Task 1.2
    list_customers_with_urgent_orders(db)

    # Task 1.3
    top_customers_by_total_price(db, top_n=10)

if __name__ == "__main__":
    main(sys.argv)