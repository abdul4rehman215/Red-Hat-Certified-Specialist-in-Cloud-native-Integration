#!/bin/bash
# Lab 18 - Using Camel Components for File Handling
# Commands Executed During Lab (sequential, no explanations)

mkdir camel-file-handling-lab
cd camel-file-handling-lab

mkdir -p src/main/java/com/example/camel
mkdir -p src/main/resources
mkdir -p input
mkdir -p output
mkdir -p processed
mkdir -p error

nano pom.xml

nano src/main/java/com/example/camel/FileHandlingApplication.java
nano src/main/java/com/example/camel/FileProcessingRoute.java

mvn clean compile

echo "Hello World from Camel File Component" > input/test1.txt
echo "This is a sample file for processing" > input/test2.txt
echo "File handling with Apache Camel" > input/test3.txt

mvn camel:run

cd ~/camel-file-handling-lab
ls -la output/
cat output/test1.txt
ls -la processed/

^C

sudo apt update
sudo apt install -y vsftpd

sudo useradd -m -s /bin/bash ftpuser
echo "ftpuser:ftppass123" | sudo chpasswd
sudo mkdir -p /home/ftpuser/ftp/upload
sudo mkdir -p /home/ftpuser/ftp/download
sudo chown -R ftpuser:ftpuser /home/ftpuser/ftp
sudo chmod -R 755 /home/ftpuser/ftp

sudo nano /etc/vsftpd.conf

sudo mkdir -p /home/ftpuser/ftp/urgent
sudo chown -R ftpuser:ftpuser /home/ftpuser/ftp/urgent

sudo systemctl restart vsftpd
sudo systemctl enable vsftpd

sudo nano /etc/ssh/sshd_config

sudo useradd -m -s /bin/false sftpuser
echo "sftpuser:sftppass123" | sudo chpasswd
sudo mkdir -p /home/sftpuser/upload
sudo mkdir -p /home/sftpuser/download
sudo chown root:root /home/sftpuser
sudo chmod 755 /home/sftpuser
sudo chown sftpuser:sftpuser /home/sftpuser/upload /home/sftpuser/download

sudo mkdir -p /home/sftpuser/archive
sudo chown sftpuser:sftpuser /home/sftpuser/archive

sudo systemctl restart ssh

nano src/main/java/com/example/camel/FtpSftpRoute.java

nano src/main/java/com/example/camel/FileHandlingApplication.java

mkdir -p ftp-upload ftp-downloaded ftp-processed ftp-sftp-errors
mkdir -p sftp-upload sftp-downloaded sftp-processed
mkdir -p sync-source sync-completed sync-processed

mvn clean compile
mvn camel:run

echo "Basic file processing test" > input/basic-test.txt
echo "Another test file" > input/basic-test2.txt

sleep 5
ls -la output/
ls -la processed/

echo "FTP upload test file" > ftp-upload/ftp-test1.txt
echo "Another FTP test" > ftp-upload/ftp-test2.txt

sleep 10
ls -la ftp-processed/

ftp localhost
# ftp> cd upload
# ftp> ls
# ftp> quit

echo "Download test from FTP" > /tmp/ftp-download-test.txt
sudo cp /tmp/ftp-download-test.txt /home/ftpuser/ftp/download/

sleep 10
ls -la ftp-downloaded/

echo "SFTP upload test file" > sftp-upload/sftp-test1.txt
echo "Another SFTP test" > sftp-upload/sftp-test2.txt

sleep 10
ls -la sftp-processed/

sftp sftpuser@localhost
# sftp> cd upload
# sftp> ls
# sftp> quit

echo "Download test from SFTP" > /tmp/sftp-download-test.txt
sudo cp /tmp/sftp-download-test.txt /home/sftpuser/download/

sleep 10
ls -la sftp-downloaded/

echo "Sync test file 1" > sync-source/sync-test1.txt
echo "Sync test file 2" > sync-source/sync-test2.txt

sleep 15
ls -la sync-completed/
ls -la sync-processed/

ftp localhost
# ftp> cd upload
# ftp> ls
# ftp> quit

sftp sftpuser@localhost
# sftp> cd upload
# sftp> ls
# sftp> quit

nano src/main/java/com/example/camel/AdvancedFileRoute.java

nano src/main/java/com/example/camel/FileHandlingApplication.java

mkdir -p advanced-input advanced-processed urgent-output archive-output regular-output
mkdir -p size-input size-processed large-files small-files
mkdir -p extension-input extension-processed text-files csv-files xml-files other-files

^C
mvn camel:run

echo "URGENT: Critical system alert" > advanced-input/urgent-file.txt
echo "ARCHIVE: Old data for storage" > advanced-input/archive-file.txt
echo "Regular processing file" > advanced-input/regular-file.txt

echo "Small file content" > size-input/small.txt
dd if=/dev/zero of=size-input/large.txt bs=1024 count=2

echo "Text content" > extension-input/test.txt
echo "Name,Age,City" > extension-input/data.csv
echo "<root><item>test</item></root>" > extension-input/config.xml
echo "Binary data" > extension-input/file.bin

sleep 10
ls -la urgent-output/ archive-output/ regular-output/
ls -la large-files/ small-files/
ls -la text-files/ csv-files/ xml-files/ other-files/

nano src/main/java/com/example/camel/MonitoringRoute.java

nano src/main/java/com/example/camel/FileHandlingApplication.java

mkdir -p monitored-input monitored-processed validated-output dead-letter-queue

^C
mvn clean compile
mvn camel:run

echo "Valid file content" > monitored-input/valid-file.txt
touch monitored-input/empty-file.txt
echo "Some content" > monitored-input/invalid-filename.txt

sleep 30
ls -la validated-output/
ls -la dead-letter-queue/

^C

sudo systemctl status vsftpd --no-pager
