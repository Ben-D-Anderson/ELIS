sudo apt -y install openjdk-11-jre
sudo apt -y install python3
sudo apt -y install python3-rpi.gpio

echo [Desktop Entry] >> /home/elis/.config/autostart/ELIS.desktop
echo Encoding=UTF-8 >> /home/elis/.config/autostart/ELIS.desktop
echo Version=1.0 >> /home/elis/.config/autostart/ELIS.desktop
echo Type=Application >> /home/elis/.config/autostart/ELIS.desktop
echo Terminal=false >> /home/elis/.config/autostart/ELIS.desktop
echo Exec=/home/elis/IMechE/start.sh >> /home/elis/.config/autostart/ELIS.desktop
echo Name=ELIS >> /home/elis/.config/autostart/ELIS.desktop
