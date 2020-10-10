
### Локальный запуск

1. Поднять базу: 
    
    в корне проекта: docker-compose up -d

1. Протестировать и собрать:
    
    ./gradlew build
    
1. Упаковать в докер:

    сd deploy
    docker-compose build
    
1. Запустить:

    cd deploy
    docker-compose up
    
2. Протестировать в ручную

    [./manual-test/requests.http](./manual-test/requests.http)

    