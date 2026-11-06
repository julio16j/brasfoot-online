"""Create a RAR 4 archive in store mode from the distribution ZIP, without dependencies."""
from pathlib import Path
import binascii
import struct
import zipfile

ROOT = Path(__file__).resolve().parents[1]


def header(kind, flags, payload=b''):
    body = struct.pack('<BHH', kind, flags, 7 + len(payload)) + payload
    return struct.pack('<H', binascii.crc32(body) & 0xffff) + body


def filename(name):
    # RAR 4 Unicode names contain an ANSI fallback followed by encoded UTF-16.
    fallback = name.encode('cp1252', errors='replace') + b'\0'
    units = name.encode('utf-16le')
    encoded = bytearray(b'\0')
    for offset in range(0, len(units), 8):
        group = units[offset:offset + 8]
        flags = sum(2 << (6 - 2 * index) for index in range(len(group) // 2))
        encoded.append(flags)
        encoded.extend(group)
    return fallback + encoded


def main():
    source = ROOT / 'dist/Brasfoot-Online-Teste.zip'
    target = source.with_suffix('.rar')
    pending = target.with_suffix('.rar.tmp')
    try:
        with zipfile.ZipFile(source) as archive, pending.open('wb') as output:
            output.write(b'Rar!\x1a\x07\x00')
            output.write(header(0x73, 0, b'\0' * 6))
            count = 0
            for entry in archive.infolist():
                if entry.is_dir():
                    continue
                if entry.file_size > 0xffffffff:
                    raise ValueError('RAR 4 store writer supports files up to 4 GiB')
                name = filename(entry.filename.replace('/', '\\'))
                year, month, day, hour, minute, second = entry.date_time
                stamp = ((year - 1980) << 25 | month << 21 | day << 16
                         | hour << 11 | minute << 5 | second // 2)
                payload = struct.pack('<IIBIIBBHI', entry.file_size, entry.file_size,
                                      2, entry.CRC, stamp, 20, 0x30, len(name), 0x20)
                output.write(header(0x74, 0x8200, payload + name))
                with archive.open(entry) as data:
                    while chunk := data.read(1024 * 1024):
                        output.write(chunk)
                count += 1
            output.write(header(0x7b, 0))
        pending.replace(target)
    finally:
        if pending.exists():
            pending.unlink()
    print(f'RAR: {target} ({count} files, {target.stat().st_size} bytes; store mode)')


if __name__ == '__main__':
    main()
