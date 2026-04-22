#!/usr/bin/env python3

from pathlib import Path
import sys


ROOT = Path(__file__).resolve().parents[1]
REQUIRED_PATHS = [
    ROOT / "backend" / "README.md",
    ROOT / "backend" / "package.json",
    ROOT / "backend" / "src" / "server.js",
    ROOT / "runs" / "20260422-backend-implementation-progress.md",
    ROOT / "handoffs" / "backend-implementation-ready.md",
]


def main() -> int:
    missing = [path for path in REQUIRED_PATHS if not path.exists()]
    if missing:
        print("Missing required implementation artifacts:")
        for path in missing:
            print(f"- {path}")
        return 1

    print("Backend implementation handoff validation passed.")
    return 0


if __name__ == "__main__":
    sys.exit(main())
