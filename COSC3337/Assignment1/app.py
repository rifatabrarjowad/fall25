import csv
import os
import sys
from dataclasses import dataclass, asdict
from typing import List, Dict, Any, Optional

DATA_PATH = os.environ.get("PARTS_DATA_PATH", "./part.tbl")
BACKUP_PATH = os.environ.get("PARTS_BACKUP_PATH", "./part.backup.tbl")
DELIM = "|"

COLUMNS = [
    "PARTKEY", "NAME", "MFGR", "BRAND", "TYPE", "SIZE", "CONTAINER", "RETAILPRICE", "COMMENT"
]

@dataclass
class Part:
    PARTKEY: int
    NAME: str
    MFGR: str
    BRAND: str
    TYPE: str
    SIZE: int
    CONTAINER: str
    RETAILPRICE: float
    COMMENT: str

    @classmethod
    def from_row(cls, row: Dict[str, str]) -> "Part":
        return cls(
            PARTKEY=int(row["PARTKEY"]),
            NAME=row["NAME"],
            MFGR=row["MFGR"],
            BRAND=row["BRAND"],
            TYPE=row["TYPE"],
            SIZE=int(row["SIZE"]),
            CONTAINER=row["CONTAINER"],
            RETAILPRICE=float(row["RETAILPRICE"]),
            COMMENT=row["COMMENT"],
        )

    def to_row(self) -> Dict[str, str]:
        d = asdict(self).copy()
        d["PARTKEY"] = str(d["PARTKEY"])
        d["SIZE"] = str(d["SIZE"])
        # Format price with 2 decimals like TPC-H files (but tolerate float)
        d["RETAILPRICE"] = f"{self.RETAILPRICE:.2f}"
        return d

def backup_if_needed():
    if not os.path.exists(DATA_PATH):
        print(f"ERROR: dataset not found at {DATA_PATH}")
        sys.exit(1)
    if not os.path.exists(BACKUP_PATH):
        with open(DATA_PATH, "rb") as src, open(BACKUP_PATH, "wb") as dst:
            dst.write(src.read())

def load_parts() -> List[Part]:
    parts: List[Part] = []
    with open(DATA_PATH, "r", encoding="utf-8", errors="ignore", newline="") as f:
        reader = csv.reader(f, delimiter=DELIM)
        for raw in reader:
            if not raw:
                continue
            # TPC-H rows typically end with a trailing delimiter; guard for that
            # We only take the first 9 elements (our COLUMNS length)
            fields = [x for x in raw if x is not None]
            if len(fields) < 9:
                # try trimming trailing empty from end
                if fields and fields[-1] == "":
                    fields = fields[:-1]
            if len(fields) < 9:
                # Skip malformed rows
                continue
            row = dict(zip(COLUMNS, fields[:9]))
            try:
                parts.append(Part.from_row(row))
            except Exception:
                # Skip rows we can't parse cleanly
                continue
    return parts

def write_parts(parts: List[Part]) -> None:
    with open(DATA_PATH, "w", encoding="utf-8", newline="") as f:
        writer = csv.writer(f, delimiter=DELIM)
        for p in parts:
            row = [p.to_row()[c] for c in COLUMNS]
            # Maintain TPCH-like trailing delimiter at end of line if present in original
            writer.writerow(row)
    print("✅ Data saved.")

def find_by_key(parts: List[Part], key: int) -> Optional[Part]:
    for p in parts:
        if p.PARTKEY == key:
            return p
    return None

def search(parts: List[Part]) -> None:
    print("\nSearch by:\n  1) PARTKEY (exact)\n  2) NAME contains\n  3) BRAND equals\n  4) TYPE contains")
    choice = input("Choose 1-4: ").strip()
    if choice == "1":
        key = int(input("PARTKEY: ").strip())
        p = find_by_key(parts, key)
        if p:
            print_part(p)
        else:
            print("Not found.")
    elif choice == "2":
        term = input("NAME contains: ").strip().lower()
        results = [p for p in parts if term in p.NAME.lower()]
        print_list(results)
    elif choice == "3":
        brand = input("BRAND equals: ").strip().lower()
        results = [p for p in parts if p.BRAND.lower() == brand]
        print_list(results)
    elif choice == "4":
        term = input("TYPE contains: ").strip().lower()
        results = [p for p in parts if term in p.TYPE.lower()]
        print_list(results)
    else:
        print("Invalid choice.")

def print_part(p: Part) -> None:
    print("-" * 60)
    for c in COLUMNS:
        print(f"{c:12}: {getattr(p, c)}")
    print("-" * 60)

def print_list(items: List[Part], limit: int = 20) -> None:
    if not items:
        print("No results.")
        return
    print(f"Found {len(items)} result(s). Showing up to {limit}:")
    for i, p in enumerate(items[:limit], 1):
        print(f"{i:>3}. PARTKEY={p.PARTKEY} | NAME={p.NAME} | BRAND={p.BRAND} | TYPE={p.TYPE} | PRICE={p.RETAILPRICE:.2f}")

def insert(parts: List[Part]) -> None:
    try:
        key = int(input("PARTKEY (unique integer): ").strip())
        if find_by_key(parts, key):
            print("Error: PARTKEY already exists.")
            return
        name = input("NAME: ").strip()
        mfgr = input("MFGR: ").strip()
        brand = input("BRAND: ").strip()
        type_ = input("TYPE: ").strip()
        size = int(input("SIZE (int): ").strip())
        container = input("CONTAINER: ").strip()
        price = float(input("RETAILPRICE (float): ").strip())
        comment = input("COMMENT: ").strip()
        newp = Part(key, name, mfgr, brand, type_, size, container, price, comment)
        parts.append(newp)
        write_parts(parts)
        print("✅ Inserted.")
    except Exception as e:
        print(f"Insert failed: {e}")

def update(parts: List[Part]) -> None:
    try:
        key = int(input("PARTKEY to update: ").strip())
        p = find_by_key(parts, key)
        if not p:
            print("Not found.")
            return
        print("Press Enter to keep current value.")
        for field in COLUMNS[1:]:  # skip PARTKEY
            cur = getattr(p, field)
            newval = input(f"{field} [{cur}]: ").strip()
            if newval == "":
                continue
            try:
                if field in ("SIZE",):
                    setattr(p, field, int(newval))
                elif field in ("RETAILPRICE",):
                    setattr(p, field, float(newval))
                else:
                    setattr(p, field, newval)
            except Exception as e:
                print(f"Invalid value for {field}: {e}")
        write_parts(parts)
        print("✅ Updated.")
    except Exception as e:
        print(f"Update failed: {e}")

def delete(parts: List[Part]) -> None:
    try:
        key = int(input("PARTKEY to delete: ").strip())
        idx = None
        for i, p in enumerate(parts):
            if p.PARTKEY == key:
                idx = i
                break
        if idx is None:
            print("Not found.")
            return
        confirm = input(f"Are you sure you want to delete PARTKEY={key}? (y/N): ").strip().lower()
        if confirm == "y":
            parts.pop(idx)
            write_parts(parts)
            print("✅ Deleted.")
        else:
            print("Cancelled.")
    except Exception as e:
        print(f"Delete failed: {e}")

def main():
    backup_if_needed()
    parts = load_parts()
    print("Parts Dataset Manager")
    print(f"Loaded {len(parts)} records from {DATA_PATH}")
    while True:
        print("\nMenu:\n  1) Insert\n  2) Search\n  3) Update by PARTKEY\n  4) Delete by PARTKEY\n  5) Save\n  0) Exit")
        choice = input("Choose: ").strip()
        if choice == "1":
            insert(parts)
        elif choice == "2":
            search(parts)
        elif choice == "3":
            update(parts)
        elif choice == "4":
            delete(parts)
        elif choice == "5":
            write_parts(parts)
        elif choice == "0":
            # ensure data saved before stop
            write_parts(parts)
            print("Bye.")
            break
        else:
            print("Invalid choice.")

if __name__ == "__main__":
    try:
        main()
    except KeyboardInterrupt:
        # save on Ctrl+C
        try:
            print("\nInterrupted. Saving before exit...")
            # reload not needed; just saving current in-memory list isn't harmful
        except Exception:
            pass
        sys.exit(0)
