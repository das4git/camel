 Релизовано чтение файлов из директории ./src/data средствами Сamel. 
 Если XML файл, отправляем в очередь queue.
 Если TXT файл, отправляем в очередь queue и записываем в БД postgresql в таблицу1 TEXT_ID, TEXT, TIME.
 Если файл иного формата, отправляем в очередь invalid.
 Подключен BitronixTransactionManager для ресурсов ActiveMQ, Postgresql.
 При обработке 100-ого файла отправляется сообщение на почту. 
 (Почту и пароль нужно прописать в файле MyRouteBuilder.java)
 Для гугл почты нужно дать разрешение по инструкции https://stackoverflow.com/questions/50098733/sending-email-with-camel/50117505
