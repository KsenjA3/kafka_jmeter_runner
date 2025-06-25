# Используем официальный образ JMeter в качестве базового
FROM alpine/jmeter:5.6.3

# Устанавливаем рабочую директорию в JMeter Home
WORKDIR /opt/apache-jmeter-5.6.3

# Копируем ваш скомпилированный JAR-файл JMeterRunner и все пользовательские Sampler'ы
# Предполагается, что ваш проект Maven компилирует все в target/dbaProject-1.0-SNAPSHOT.jar
# Измените путь и имя JAR-файла на то, что соответствует вашему проекту.
# Если у вас несколько JAR-файлов, скопируйте их все сюда.
COPY target/dbaProject-1.0-SNAPSHOT.jar /opt/apache-jmeter-5.6.3/lib/ext/kafka_samplers.jar
# Если у вас есть другие библиотеки, которые нужны для JMeterRunner или Sampler'ов:
# COPY path/to/your/dependencies/*.jar /opt/apache-jmeter/lib/ext/

# Копируем JMX-файл и другие ресурсы тестов
COPY jmeter_tests/ /jmeter_tests/

# Устанавливаем ENTRYPOINT на ваше Java-приложение
# Это позволяет нам передавать аргументы непосредственно вашему JMeterRunner
# Имя класса должно быть полным (включая пакет)
#ENTRYPOINT ["java", "-jar", "/opt/apache-jmeter/lib/ext/kafka_samplers.jar"]

# Или, если ваш JAR не самоисполняемый (без Main-Class в MANIFEST.MF),
# используйте такую строку (предпочтительнее, если вы будете запускать JMeterRunner, а не ApacheJMeter.jar):
# CMD ["java", "-cp", "/opt/apache-jmeter/lib/ext/kafka_samplers.jar:/opt/apache-jmeter/lib/*:/opt/apache-jmeter/lib/ext/*", "org.solutions.jmeter_solution3.sender.JMeterRunner"]

# Мы запускаем Java напрямую, указывая полный Classpath
# и затем полный путь к вашему основному классу (JMeterRunner).
#CMD ["java", "-cp", "/opt/apache-jmeter-5.6.3/lib/*:/opt/apache-jmeter-5.6.3/lib/ext/*:/opt/apache-jmeter-5.6.3/lib/ext/kafka_samplers.jar", "org.solutions.jmeter_solution3.sender.JMeterRunner"]

# Альтернативный CMD для интерактивной работы, если ENTRYPOINT не используется
# CMD ["/bin/bash"]