#!/bin/bash
# Lab 01 - Setting Up the Camel Environment
# Commands Executed During Lab (Sequential)

# -------------------------------
# Task 1.1 - Verify Java
# -------------------------------
java -version
javac -version
echo $JAVA_HOME

export JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64
echo 'export JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64' >> ~/.bashrc
source ~/.bashrc
echo $JAVA_HOME

# -------------------------------
# Task 1.2 - Create Workspace Structure
# -------------------------------
mkdir -p ~/camel-lab
cd ~/camel-lab
mkdir -p downloads bin config logs
ls -la

# -------------------------------
# Task 1.3 - Download and Install Apache Karaf
# -------------------------------
cd ~/camel-lab/downloads
wget https://archive.apache.org/dist/karaf/4.4.3/apache-karaf-4.4.3.tar.gz

tar -xzf apache-karaf-4.4.3.tar.gz
mv apache-karaf-4.4.3 ~/camel-lab/karaf

export KARAF_HOME=~/camel-lab/karaf
echo 'export KARAF_HOME=~/camel-lab/karaf' >> ~/.bashrc
echo 'export PATH=$PATH:$KARAF_HOME/bin' >> ~/.bashrc
source ~/.bashrc
echo $KARAF_HOME

# -------------------------------
# Task 1.4 - Download and Install Apache ActiveMQ
# -------------------------------
wget https://archive.apache.org/dist/activemq/5.17.2/apache-activemq-5.17.2-bin.tar.gz

tar -xzf apache-activemq-5.17.2-bin.tar.gz
mv apache-activemq-5.17.2 ~/camel-lab/activemq

export ACTIVEMQ_HOME=~/camel-lab/activemq
echo 'export ACTIVEMQ_HOME=~/camel-lab/activemq' >> ~/.bashrc
echo 'export PATH=$PATH:$ACTIVEMQ_HOME/bin' >> ~/.bashrc
source ~/.bashrc
echo $ACTIVEMQ_HOME

# -------------------------------
# Task 1.5 - Download and Install Apache Camel
# -------------------------------
wget https://archive.apache.org/dist/camel/apache-camel/3.20.7/apache-camel-3.20.7.tar.gz

tar -xzf apache-camel-3.20.7.tar.gz
mv apache-camel-3.20.7 ~/camel-lab/camel

export CAMEL_HOME=~/camel-lab/camel
echo 'export CAMEL_HOME=~/camel-lab/camel' >> ~/.bashrc
echo 'export PATH=$PATH:$CAMEL_HOME/bin' >> ~/.bashrc
source ~/.bashrc
echo $CAMEL_HOME

# -------------------------------
# Task 2.1 - Start ActiveMQ
# -------------------------------
cd $ACTIVEMQ_HOME
./bin/activemq start
./bin/activemq status

# -------------------------------
# Task 2.2 - Start Karaf
# -------------------------------
cd $KARAF_HOME
./bin/karaf

# -------------------------------
# Task 2.3 - Karaf Console Commands (Camel Features)
# -------------------------------
# feature:repo-add camel 3.20.7
# feature:install camel-core
# feature:install camel-blueprint
# feature:install camel-jms
# feature:install camel-activemq
# feature:list | grep camel

# -------------------------------
# Task 2.4 - Create Blueprint XML Config
# -------------------------------
mkdir -p ~/camel-lab/config
cd ~/camel-lab/config
nano camel-context.xml
ls -la

# -------------------------------
# Task 3.1 - Deploy Blueprint to Karaf
# -------------------------------
cp ~/camel-lab/config/camel-context.xml $KARAF_HOME/deploy/

# -------------------------------
# Task 3.1 - Karaf Console Verification Commands
# -------------------------------
# bundle:list | grep camel-context
# camel:route-list

# -------------------------------
# Task 3.2 - Create Input/Output and Test File
# -------------------------------
mkdir -p ~/camel-lab/input ~/camel-lab/output
echo "Hello from Apache Camel! This is a test message." > ~/camel-lab/input/test-message.txt
ls -la ~/camel-lab/input

# -------------------------------
# Task 3.3 - Verify Output
# -------------------------------
ls -la ~/camel-lab/output/
cat ~/camel-lab/output/test-message.txt

# -------------------------------
# Task 3.4 - Karaf Console Route Statistics
# -------------------------------
# camel:route-info file-to-jms-route
# camel:route-info jms-to-file-route

# -------------------------------
# Task 3.5 - Test with Multiple Files
# -------------------------------
for i in {1..5}; do
  echo "Test message number $i - $(date)" > ~/camel-lab/input/message-$i.txt
done

ls -la ~/camel-lab/input/
watch -n 2 "ls -la ~/camel-lab/output/"
