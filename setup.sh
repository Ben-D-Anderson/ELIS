sudo apt install openjdk-11-jre
wget https://snapshot.debian.org/archive/debian/20190501T215844Z/pool/main/g/glibc/multiarch-support_2.28-10_armhf.deb
sudo apt install ./multiarch-support_2.28-10_armhf.deb
wget https://snapshot.debian.org/archive/debian/20141009T042436Z/pool/main/libj/libjpeg8/libjpeg8_8d1-2_armhf.deb
sudo apt install ./libjpeg8_8d1-2_armhf.deb

echo STILL NEED TO ENABLE LEGACY CAMERA SUPPORT
#enabled legacy camera support in raspi-config -> interfaces
