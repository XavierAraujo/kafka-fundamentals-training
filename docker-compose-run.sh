docker-compose up &

echo ""
echo ""
echo ""
echo "Waiting for Kafka broker initialization"
sleep 10

echo "Setting up Kafka topics"
docker cp ./scripts/kafka-setup.sh kafka-fundamentals-training-kafka:/kafka-setup.sh
docker exec -it kafka-fundamentals-training-kafka /kafka-setup.sh
