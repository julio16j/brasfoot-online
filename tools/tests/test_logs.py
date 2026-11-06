"""Temporary diagnostics: callers finish child processes before exit cleanup runs."""
import atexit
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
CRASH_PATTERNS = ("hs_err_pid*.log", "replay_pid*.log")
_existing = {p for pattern in CRASH_PATTERNS for p in ROOT.glob(pattern)}
_logs = []

def open_log(path):
    stream = path.open("w", encoding="utf-8")
    _logs.append((path, stream))
    return stream

def cleanup():
    for path, stream in _logs:
        stream.close()
        path.unlink(missing_ok=True)
    for pattern in CRASH_PATTERNS:
        for path in ROOT.glob(pattern):
            if path not in _existing:
                path.unlink(missing_ok=True)

atexit.register(cleanup)
