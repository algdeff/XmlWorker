������ ������� � ������ XmlWorker
(������ ������������ �� JDK 8u121, MySQL Server 5.7.17)

���� ��� maven � JRE, 1-4 ��� ���:
1. ��������� maven: http://maven.apache.org/download.cgi
2. ������������� ����� � maven �� ����
3. ��������� � ���������� %PATH% ���� � maven/bin, �������� c:\apache-maven-3.3.9\bin\
4. ��������� � ������������� JDK8 ��� ����� �������: http://www.oracle.com/technetwork/java/javase/downloads/2133151
5. ��������� � ���������� %PATH% � %JAVA_HOME% ���� � JDK
5. ��������� � ���������� XmlWorker
6. ��� ������ ������� � ������� maven �����: mvn compile
7. ����� ������ ��������� ��������������� SQL �������� ��� ���������� ����� ����� �� � ������� � ���.

===============================================================================================================
C������� ����� ����� � ������� � ���� ������ (scriptSQL.txt):
����� ������� � ������� SQL �������, �������� �� Wjrkbench ��� � ������� ������� ��:
1. CREATE SCHEMA `my_test` ;
2. CREATE TABLE `my_test`.`test` ( `id` INT NOT NULL AUTO_INCREMENT, `FIELD` INT NULL, PRIMARY KEY (`id`));

PS. ���� ����� �� ������, �� ���������� ��������� �� ��� ������ "my_test" � ������� SQL � ��������
��������������� ������� hibernate.cfg.xml � TestEntity.hbm.xml
===============================================================================================================

8. ��������� URL ������� ��, ������ � ������, � ����� hibernate.cfg.xml:
<property name="hibernate.connection.url">jdbc:mysql://localhost:3306/my_test?useSSL=false&amp;serverTimezone=UTC</property>
<property name="hibernate.connection.username">user</property>
<property name="hibernate.connection.password">pass</property>

9. ��������� XmlWorker: mvn exec:java -Dexec.mainClass="XmlWorker.ServerStarter"
10. ����� �� ���������: CTRL+C

����� ���-�� ������� ������ � ���� ����������� .jar ����, �� ����� �������� � �������������:
1. ������� ����������� .jar: mvn package
2. ���� �������� � target/XmlWorker-......-jar-with-dependencies.jar (����. ��������� ������� 'maven-assembly-plugin' � pom.xml)
3. ���� ����� ����������� � ����� ����� (������� �� "progFolder")
4. � ����� � ������ (progFolder) ����������� ������ ��������� ���� hibernate.cfg.xml c ����������� ������� � ��
5. ���-�� � progFolder ����� ��������� ����� �������� xmlworker.conf.xml � TestEntity.hbm.xml, ����� ����� ��������� �� ���������
6. � ���������, ������ .jar �� JVM: java -jar XmlWorker-..(������)..-jar-with-dependencies.jar

===============================================================================================================
��������� ������� � �� ��� ������, ��� ������������ �����, �������� "org_db":

hibernate.cfg.xml
<property name="hibernate.connection.url">jdbc:mysql://localhost:3306/org_db?useSSL=false&amp;serverTimezone=UTC</property>

���� ��������� ������ "TimeZone... UTC" �� ����� ����� ��(org_db) ��������� �������� "?serverTimezone=UTC"
���� ���� ������ Postgre �� ������ MySQLInnoDBDialect �� PostgreSQL94Dialect � ������:
<property name="hibernate.dialect">org.hibernate.dialect.MySQLInnoDBDialect</property>

TestEntity.hbm.xml
<class name="XmlWorker.HibernateEntities.TestEntity" table="test" schema="org_db">

SQL:
CREATE TABLE `org_db`.`test` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `FIELD` INT NULL,
  PRIMARY KEY (`id`));


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