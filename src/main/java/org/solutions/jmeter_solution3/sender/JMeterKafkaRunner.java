package org.solutions.jmeter_solution3.sender;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JMeterKafkaRunner {

    public static void main(String[] args) {
        String jmxFileName = "kafka-sender33.jmx"; // Имя вашего JMeter-файла
        String resultsFileName = "results33.jtl"; // Имя файла для результатов JMeter
        String logFileName = "jmeter33.log";    // Имя файла для логов JMeter

        // Путь к директории, где лежат jmx-файлы и будут сохраняться результаты
        // Это относительный путь от того места, где будет запускаться Java-приложение
        // Предполагаем, что оно запускается из того же каталога, что и docker-compose.yml
        String hostResultsDir = "./jmeter_tests"; // Это наша смонтированная папка

        System.out.println("Запуск JMeter-теста для Kafka...");
        // Создаем команду для запуска JMeter внутри Docker-контейнера
        // -n: режим без GUI
        // -t: путь к JMX-файлу (внутри контейнера)
        // -l: путь к файлу результатов (внутри контейнера)
        // -j: путь к файлу логов JMeter (внутри контейнера)

        try {

//            String command = String.format(
//                            "docker exec -w /jmeter_tests jmeter /bin/bash -c \"/opt/apache-jmeter-5.6.3/bin/jmeter -n -t %s -l %s -j %s\"",
//                               jmxFileName, resultsFileName, logFileName);
//            System.out.println("Выполняемая команда: " + command);
//            Process process = Runtime.getRuntime().exec(command);

            // Создаем список аргументов для команды bash
            List<String> commandParts = new ArrayList<>();
            commandParts.add("/bin/bash");
            commandParts.add("-c");
            // Весь остальной вызов JMeter - это один аргумент для -c
            commandParts.add(String.format(
                    "/opt/apache-jmeter-5.6.3/bin/jmeter -n -t %s -l %s -j %s",
                    jmxFileName, resultsFileName, logFileName
            ));

            // Создаем ProcessBuilder
            ProcessBuilder pb = new ProcessBuilder(commandParts);

            // Установим рабочую директорию, если это необходимо
            // В вашем docker-compose.yml уже есть working_dir: /jmeter_tests,
            // поэтому это может быть не строго необходимо, но хорошая практика.
            pb.directory(new File("/jmeter_tests"));

            System.out.println("Выполняемая команда: " + String.join(" ", pb.command()));
            Process process = pb.start(); // Запускаем процесс


            // Чтение вывода (stdout) процесса
            new Thread(() -> {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.out.println("STDOUT: " + line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

            // Чтение ошибок (stderr) процесса
            new Thread(() -> {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.err.println("STDERR: " + line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

            int exitCode = process.waitFor(); // Ждем завершения процесса
            System.out.println("JMeter тест завершен с кодом выхода: " + exitCode);

            if (exitCode == 0) {
                System.out.println("Тест JMeter успешно завершен.");
                System.out.println("Результаты теста находятся в файле: " + hostResultsDir + File.separator + resultsFileName);
                System.out.println("Логи JMeter находятся в файле: " + hostResultsDir + File.separator + logFileName);
                System.out.println("Для анализа результатов откройте " + hostResultsDir + File.separator + resultsFileName + " в JMeter GUI.");
            } else {
                System.err.println("Ошибка при выполнении JMeter теста. Код выхода: " + exitCode);
            }

        } catch (IOException | InterruptedException e) {
            System.err.println("Произошла ошибка при запуске JMeter: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Опционально: Остановка Docker Compose после завершения теста
            // System.out.println("Остановка Docker Compose...");
            // try {
            //     Process stopProcess = Runtime.getRuntime().exec("docker-compose down");
            //     stopProcess.waitFor();
            //     System.out.println("Docker Compose остановлен.");
            // } catch (IOException | InterruptedException e) {
            //     System.err.println("Ошибка при остановке Docker Compose: " + e.getMessage());
            // }
        }
    }
}