docker-compose up &

echo ""
echo ""
echo ""
echo "Waiting for Kafka broker initialization"
sleep 30

echo "Setting up Kafka topics"
docker cp ./scripts/kafka-setup.sh kafka-fundamentals-training-kafka:/kafka-setup.sh
docker exec -it kafka-fundamentals-training-kafka /kafka-setup.sh

GREEN='\033[0;32m'
NC='\033[0m'
echo ""
echo ""
echo "${GREEN}#####################################${NC}"
echo "${GREEN}# Finished setting up training data #${NC}"
echo "${GREEN}#####################################${NC}"
