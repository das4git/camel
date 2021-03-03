 Релизовано чтение файлов из директории ./src/data средствами Сamel. 
 Если XML файл, отправляем в очередь queue
 Если TXT файл, отправляем в очередь queue и записываем в БЛ postgresql в таблицу1 TEXT_ID, TEXT, TIME
 Если файл иного формата, отправляем в очередь invalid
 Подключен BitronixTransactionManager для ресурсов ActiveMQ, Postgresql
