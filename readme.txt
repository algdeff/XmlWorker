сборка проекта и запуск XmlWorker
(проект тестировался на JDK 8u121, MySQL Server 5.7.17)

Если нет maven и JRE, 1-4 для Вас:
1. скачиваем maven: http://maven.apache.org/download.cgi
2. распаковываем архив с maven на диск
3. добавляем в переменные %PATH% путь к maven/bin, например c:\apache-maven-3.3.9\bin\
4. скачиваем и устанавливаем JDK8 для Вашей системы: http://www.oracle.com/technetwork/java/javase/downloads/2133151
5. добавляем в переменные %PATH% и %JAVA_HOME% путь к JDK
5. переходим в директорию XmlWorker
6. для сборки проекта с помошью maven пишем: mvn compile
7. после сборки запускаем нижеприведенные SQL комманды для добавления новой схемы БД и таблицы в ней.

===============================================================================================================
Cоздание новой схемы и таблицы в базе данных (scriptSQL.txt):
можно создать с помошью SQL запроса, например из Wjrkbench или в консоли сервера БД:
1. CREATE SCHEMA `my_test` ;
2. CREATE TABLE `my_test`.`test` ( `id` INT NOT NULL AUTO_INCREMENT, `FIELD` INT NULL, PRIMARY KEY (`id`));

PS. Если схема БД другая, то необходимо прописать ее имя вместо "my_test" в скрипте SQL и поменять
нижеприведенные конфиги hibernate.cfg.xml и TestEntity.hbm.xml
===============================================================================================================

8. настройка URL сервера БД, логина и пароля, в файле hibernate.cfg.xml:
<property name="hibernate.connection.url">jdbc:mysql://localhost:3306/my_test?useSSL=false&amp;serverTimezone=UTC</property>
<property name="hibernate.connection.username">user</property>
<property name="hibernate.connection.password">pass</property>

9. запускаем XmlWorker: mvn exec:java -Dexec.mainClass="XmlWorker.ServerStarter"
10. выход из программы: CTRL+C

Можно так-же собрать проект в один исполняемый .jar файл, со всеми классами и зависимостями:
1. Создаем исполняемый .jar: mvn package
2. Файл появится в target/XmlWorker-......-jar-with-dependencies.jar (прим. настройки плагина 'maven-assembly-plugin' в pom.xml)
3. Файл можно переместить в любую папку (назовем ее "progFolder")
4. В папке с файлом (progFolder) обязательно должен находится файл hibernate.cfg.xml c настройками доступа к БД
5. Так-же в progFolder можно поместить файлы настроек xmlworker.conf.xml и TestEntity.hbm.xml, иначе будут настройки по умолчанию
6. И посдеднее, запуск .jar на JVM: java -jar XmlWorker-..(версия)..-jar-with-dependencies.jar

===============================================================================================================
Настройка доступа к БД для другой, уже существующей схемы, например "org_db":

hibernate.cfg.xml
<property name="hibernate.connection.url">jdbc:mysql://localhost:3306/org_db?useSSL=false&amp;serverTimezone=UTC</property>

если возникает ошибка "TimeZone... UTC" то после имени БД(org_db) добавляем параметр "?serverTimezone=UTC"
Если база данных Postgre то меняем MySQLInnoDBDialect на PostgreSQL94Dialect в записи:
<property name="hibernate.dialect">org.hibernate.dialect.MySQLInnoDBDialect</property>

TestEntity.hbm.xml
<class name="XmlWorker.HibernateEntities.TestEntity" table="test" schema="org_db">

SQL:
CREATE TABLE `org_db`.`test` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `FIELD` INT NULL,
  PRIMARY KEY (`id`));


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