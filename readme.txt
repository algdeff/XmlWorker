сборка проекта и запуск XmlWorker

Если нет maven и JRE, 1-4 для Вас:
1. скачиваем maven: http://maven.apache.org/download.cgi
2. распаковываем архив с maven на диск
3. добавляем в переменные %PATH% путь к maven/bin, например c:\apache-maven-3.3.9\bin\
4. скачиваем и устанавливаем JRE для Вашей системы: http://www.oracle.com/technetwork/java/javase/downloads/index.html
5. переходим в директорию XmlWorker
6. для сборки проекта с помошью maven пишем: mvn compile
7. после сборки переходим в target/classes/ и настраиваем нижеприведенные конфиги xmlmonitor.conf.xml, hibernate.cfg.xml
8. cоздаем в базе данных таблицу 'test', пример внизу
7. запускаем монитор: mvn exec:java -Dexec.mainClass="XmlWorker.ServerStarter"
8. выход из программы: CTRL+C

Можно так-же собрать проект в один исполняемый .jar файл, со всеми классами и зависимостями:
1. Создаем исполняемый .jar: mvn package
2. Файл появится в target/XmlWorker-......-jar-with-dependencies.jar (прим. настройки плагина 'maven-assembly-plugin' в pom.xml)
3. Файл можно переместить в любую папку (назовем ее "progFolder")
4. В папке с файлом (progFolder) обязательно должен нажодится hibernate.cfg.xml c настройками доступа к БД
5. Так-же в progFolder можно поместить файл настроек xmlworker.conf.xml, иначе будут настройки по умолчанию
6. И посдеднее запуск .jar на JVM: java -jar XmlWorker-..(версия)..-jar-with-dependencies.jar


=============================================================================================
Cоздание таблицы в базе данных: scriptSQL.txt

можно создать с помошью SQL запроса:
CREATE TABLE `scheme_name`.`test` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `FIELD` INT NULL,
  PRIMARY KEY (`id`));


=============================================================================================
Настройка доступа к БД: hibernate.cfg.xml

<property name="hibernate.connection.url">jdbc:mysql://databaseURL:3306/scheme_name</property>
если возникает ошибка "TimeZone... UTC" то после имени БД(scheme_name) добавляем "?serverTimezone=UTC"

Если база данных Postgre то меняем MySQLInnoDBDialect на PostgreSQL94Dialect в записи:
<property name="hibernate.dialect">org.hibernate.dialect.MySQLInnoDBDialect</property>


=============================================================================================
Конфигурационный файл: xmlworker.conf.xml

должен быть в директории приложения, а для maven сборки в target/classes/
Путь мониторинга, результатов обработки и других настроек в нем:
    <!--путь папки с корректно обработанными файлами-->
    <processed_files_path>_xmls</processed_files_path>

    <!--путь и имя файла с данными завершенных операций-->
    <log_file_path_name>result.log</log_file_path_name>

    <!--название сущности рабочей таблицы hibernate-->
    <hdb_table_name>TestEntity</hdb_table_name>

    <!--название рабочего поля в таблице-->
    <hdb_data_field_name>field</hdb_data_field_name>

    <!--название секции записи и входящих в нее полей-->
    <xml_root_entry>Entries</xml_root_entry>
    <xml_entry_name>Entry</xml_entry_name>
    <xml_entry_content>field</xml_entry_content>

    <!--N, число генерируемых записей-->
    <num_generated_entries>5000</num_generated_entries>

    <!--количество потоков для параллельных вычислений-->
    <thread_pool_size>50</thread_pool_size>

    <!--маски обрабатываемых файлов-->
    <target_file_type_glob>*.{xml}</target_file_type_glob>