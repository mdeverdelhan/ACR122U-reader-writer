# ACR122U-reader-writer

Here is a simple Java program to read/write Mifare RFID tags with an ACR122U device.

### Features

  * Read/dump Mifare Classic tags
  * Write to Mifare Classic tags (block-wise)
  * ACR122U compliant
  * Supported tags: Mifare Classic 1K (only)
  * JRE 7.0 or later
  * MIT Licensed

### Build

```bash
~$ mvn clean install
```

### Usage

```bash
~$ java -jar ./acr122urw.jar -h
Usage: java -jar acr122urw.jar [option]
Options:
    -h, --help                      show this help message and exit
    -d, --dump [KEYS...]            dump Mifare Classic 1K cards using KEYS
    -w, --write S B KEY DATA        write DATA to sector S, block B of Mifare Classic 1K cards using KEY
Examples:
    java -jar acr122urw.jar --dump FF00A1A0B000 FF00A1A0B001 FF00A1A0B099
    java -jar acr122urw.jar --write 13 2 FF00A1A0B001 FFFFFFFFFFFF00000000060504030201
```

## About the ACR122U reader/writer

![ACR122U NFC reader/writer](res/acr122u_reader_writer.png?raw=true)

The [ACR122U NFC Reader](http://www.acs.com.hk/en/products/3/acr122u-usb-nfc-reader/) is made by [Advanced Card Systems Ltd](http://www.acs.com.hk/) (Hong Kong, China).

### Device features

  * PC-linked contactless smart card ([NFC](http://en.wikipedia.org/wiki/Near_field_communication)) reader/writer
  * Contactless operating frequency: 13.56 MHz
  * Supports: [ISO14443](http://en.wikipedia.org/wiki/ISO/IEC_14443) Type A & B, [MIFARE®](http://en.wikipedia.org/wiki/MIFARE), FeliCa, 4 types of NFC (ISO/IEC18092) tags
  * Interface: USB
  * Operating Distance: Up to 50 mm (depends on the tag type)
  * Operating Voltage: DC 5.0V
  * Operating Frequency: 13.56 MHz
  * Compliance/Certifications: ISO 14443, PC/SC, CCID
  * Size: 98 mm x 65 mm x 12.8 mm
  * Weight: 70 g
  
## Notes

### System requirements

```bash
~# # For Debian Testing
~# echo "install pn533 /bin/false" >> /etc/modprobe.d/blacklist-nfc.conf
~# echo "install nfc /bin/false" >> /etc/modprobe.d/blacklist-nfc.conf
~# modprobe -r pn533 nfc
~# apt-get install libpcsclite1 libccid pcscd libacsccid1 pcsc-tools
~# pcscd -f
```

### Donations

Bitcoin address: 13BMqpqbzJ62LjMWcPGWrTrdocvGqifdJ3
