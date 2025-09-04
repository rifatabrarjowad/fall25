# Parts Dataset Manager (Python CLI)

A simple command-line application to **read and modify** a parts dataset stored in a TPC-H–style `part.tbl` (pipe `|` delimited) file.

It supports:
- **Insert** new part
- **Search** by `PARTKEY` (exact), `NAME` (contains), `BRAND` (equals), `TYPE` (contains)
- **Update** a part by `PARTKEY`
- **Delete** a part by `PARTKEY`
- **Persistence**: the dataset is **loaded at startup** and **saved after each change** (and on exit). A one-time backup is created the first time you run it.

## Dataset Format

Expected columns in order (TPC-H `part` table):
```
PARTKEY | NAME | MFGR | BRAND | TYPE | SIZE | CONTAINER | RETAILPRICE | COMMENT
```
Delimited by `|`. Lines may have a trailing `|` in the original TPC-H data; this app tolerates that.

## Files

- `./part.tbl` – your working dataset (loaded/saved by the app)
- `./part.backup.tbl` – a one-time backup created on first run
- `app.py` – the CLI application

## How to Run

### Option A — Run directly (Linux/macOS/WSL)
```bash
python3 app.py
# or make it executable
chmod +x app.py
./app.py
```

### Option B — Choose a different dataset path
By default, the app uses `./part.tbl`. To point to your own file:
```bash
export PARTS_DATA_PATH="./part.tbl"
export PARTS_BACKUP_PATH="./part.backup.tbl"
python3 app.py
```

## Usage Guide

On launch you’ll see a menu:

```
Menu:
  1) Insert
  2) Search
  3) Update by PARTKEY
  4) Delete by PARTKEY
  5) Save
  0) Exit
```

- **Insert**: prompts for all fields. `PARTKEY` must be unique, `SIZE` is integer, `RETAILPRICE` is float.
- **Search**: pick one of four modes:
  - PARTKEY — exact match
  - NAME — substring match (case-insensitive)
  - BRAND — exact (case-insensitive)
  - TYPE — substring match (case-insensitive)
- **Update by PARTKEY**: press Enter to keep existing value for a field.
- **Delete by PARTKEY**: confirms before deleting.
- **Save**: forces a write immediately (the app also saves automatically after insert/update/delete and on exit).

## Design & Architecture

- **Language**: Python 3 (standard library only).
- **Storage**: Plain-text `|`-delimited file compatible with TPC-H `part.tbl`. The app keeps an in-memory list of `Part` dataclass rows and writes full file on each change to keep things simple and robust.
- **Data Model**: `Part` dataclass with typed fields; parsing converts `PARTKEY` and `SIZE` to `int`, and `RETAILPRICE` to `float`.
- **Resilience**:
  - Skips malformed rows on load (rather than crashing).
  - Creates a one-time backup of the original dataset (`part.backup.tbl`).
  - Saves on explicit "Save", after every mutation (Insert/Update/Delete), and when exiting (incl. Ctrl+C with best effort).
- **Extensibility**: You can easily add more search filters or export features (JSON/CSV) by reusing the `Part` model and the reader/writer functions.



**Author(s)**: Rifat Abrar Jowad
