������ ������� � ������ XmlWorker

���� ��� maven � JRE, 1-4 ��� ���:
1. ��������� maven: http://maven.apache.org/download.cgi
2. ������������� ����� � maven �� ����
3. ��������� � ���������� %PATH% ���� � maven/bin, �������� c:\apache-maven-3.3.9\bin\
4. ��������� � ������������� JRE ��� ����� �������: http://www.oracle.com/technetwork/java/javase/downloads/index.html
5. ��������� � ���������� XmlWorker
6. ��� ������ ������� � ������� maven �����: mvn compile
7. ����� ������ ��������� � target/classes/ � ����������� ��������������� ������� xmlmonitor.conf.xml, hibernate.cfg.xml
8. c������ � ���� ������ ������� 'test', ������ �����
7. ��������� �������: mvn exec:java -Dexec.mainClass="XmlWorker.ServerStarter"
8. ����� �� ���������: CTRL+C

����� ���-�� ������� ������ � ���� ����������� .jar ����, �� ����� �������� � �������������:
1. ������� ����������� .jar: mvn package
2. ���� �������� � target/XmlWorker-......-jar-with-dependencies.jar (����. ��������� ������� 'maven-assembly-plugin' � pom.xml)
3. ���� ����� ����������� � ����� ����� (������� �� "progFolder")
4. � ����� � ������ (progFolder) ����������� ������ ��������� hibernate.cfg.xml c ����������� ������� � ��
5. ���-�� � progFolder ����� ��������� ���� �������� xmlworker.conf.xml, ����� ����� ��������� �� ���������
6. � ��������� ������ .jar �� JVM: java -jar XmlWorker-..(������)..-jar-with-dependencies.jar


=============================================================================================
C������� ������� � ���� ������: scriptSQL.txt

����� ������� � ������� SQL �������:
CREATE TABLE `scheme_name`.`test` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `FIELD` INT NULL,
  PRIMARY KEY (`id`));


=============================================================================================
��������� ������� � ��: hibernate.cfg.xml

<property name="hibernate.connection.url">jdbc:mysql://databaseURL:3306/scheme_name</property>
���� ��������� ������ "TimeZone... UTC" �� ����� ����� ��(scheme_name) ��������� "?serverTimezone=UTC"

���� ���� ������ Postgre �� ������ MySQLInnoDBDialect �� PostgreSQL94Dialect � ������:
<property name="hibernate.dialect">org.hibernate.dialect.MySQLInnoDBDialect</property>


=============================================================================================
���������������� ����: xmlworker.conf.xml

������ ���� � ���������� ����������, � ��� maven ������ � target/classes/
���� �����������, ����������� ��������� � ������ �������� � ���:
    <!--���� ����� � ��������� ������������� �������-->
    <processed_files_path>_xmls</processed_files_path>

    <!--���� � ��� ����� � ������� ����������� ��������-->
    <log_file_path_name>result.log</log_file_path_name>

    <!--�������� �������� ������� ������� hibernate-->
    <hdb_table_name>TestEntity</hdb_table_name>

    <!--�������� �������� ���� � �������-->
    <hdb_data_field_name>field</hdb_data_field_name>

    <!--�������� ������ ������ � �������� � ��� �����-->
    <xml_root_entry>Entries</xml_root_entry>
    <xml_entry_name>Entry</xml_entry_name>
    <xml_entry_content>field</xml_entry_content>

    <!--N, ����� ������������ �������-->
    <num_generated_entries>5000</num_generated_entries>

    <!--���������� ������� ��� ������������ ����������-->
    <thread_pool_size>50</thread_pool_size>

    <!--����� �������������� ������-->
    <target_file_type_glob>*.{xml}</target_file_type_glob>