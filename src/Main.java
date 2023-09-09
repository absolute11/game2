import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import java.util.zip.ZipInputStream;

public class Main {
    public static void main(String[] args) {
        GameProgress game1 = new GameProgress(100, 3, 10, 123.45);
        GameProgress game2 = new GameProgress(80, 2, 8, 98.76);
        GameProgress game3 = new GameProgress(120, 4, 15, 200.0);

        saveGame("D:\\Games\\savegames\\save1.dat", game1);
        saveGame("D:\\Games\\savegames\\save2.dat", game2);
        saveGame("D:\\Games\\savegames\\save3.dat", game3);
        zipFiles("D:\\Games\\savegames\\savegames.zip",
                new String[]{
                        "D:\\Games\\savegames\\save1.dat",
                        "D:\\Games\\savegames\\save2.dat",
                        "D:\\Games\\savegames\\save3.dat"
                });

        // Удаляем исходные файлы сохранений
        deleteGameFiles(new String[]{
                "D:\\Games\\savegames\\save1.dat",
                "D:\\Games\\savegames\\save2.dat",
                "D:\\Games\\savegames\\save1.dat"
        });
        openZip("D:\\Games\\savegames\\savegames.zip", "D:\\Games\\savegames");
        GameProgress gameProgress = openProgress("D:\\Games\\savegames\\save2.dat");
        System.out.println("Загруженный прогресс игры: " + gameProgress.toString());
    }
    public static void saveGame(String filePath, GameProgress game) {
        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(filePath))) {
            outputStream.writeObject(game);
            System.out.println("Игра сохранена в" + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void zipFiles(String zipFilePath, String[] filesToZip) {
        try (FileOutputStream fos = new FileOutputStream(zipFilePath);
             ZipOutputStream zipOut = new ZipOutputStream(fos)) {
            for (String filePath : filesToZip) {
                File fileToZip = new File(filePath);
                FileInputStream fis = new FileInputStream(fileToZip);
                ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
                zipOut.putNextEntry(zipEntry);

                byte[] bytes = new byte[1024];
                int length;
                while ((length = fis.read(bytes)) >= 0) {
                    zipOut.write(bytes, 0, length);
                }

                fis.close();
                fileToZip.delete(); // Удалить исходный файл после архивации
            }

            System.out.println("Файлы заархивированы в" + zipFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void deleteGameFiles(String[] filesToDelete) {
        for (String filePath : filesToDelete) {
            File fileToDelete = new File(filePath);
            if (fileToDelete.delete()) {
                System.out.println("Удален: " + filePath);
            } else {
                System.out.println("Ошибка удаления: " + filePath);
            }
        }

    }




    public static void openZip(String zipFilePath, String destinationFolder) {
        try (FileInputStream fis = new FileInputStream(zipFilePath);
             ZipInputStream zipIn = new ZipInputStream(fis)) {
            ZipEntry entry;
            while ((entry = zipIn.getNextEntry()) != null) {
                String entryName = entry.getName();
                File entryFile = new File(destinationFolder, entryName);

                try (FileOutputStream fos = new FileOutputStream(entryFile)) {
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = zipIn.read(buffer)) != -1) {
                        fos.write(buffer, 0, bytesRead);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static GameProgress openProgress(String progressFilePath) {
        GameProgress gameProgress = null;
        try (FileInputStream fis = new FileInputStream(progressFilePath);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            gameProgress = (GameProgress) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return gameProgress;
    }


}