nohup java -jar neural-1.0-SNAPSHOT-jar-with-dependencies.jar \
  --startTemperature=0.9 \
  --stopTemperature=0.1 \
  --alpha=0.95 \
  --maxTicks=200 \
  --iterationsPerTemperature=1000 \
  --trainingSize=500  \
  --networkFilename="network_out" \
  --percentNeuronsChange=5 \
   > out 2>&1 &



--startTemperature=0.9  --stopTemperature=0.1  --alpha=0.95  --maxTicks=200   --iterationsPerTemperature=1000  --trainingSize=500 --networkFilename="network_out" --percentNeuronsChange=5



  java -Xss4m -jar target/neural-1.0-SNAPSHOT-jar-with-dependencies.jar -r -f="network_out_back"