package com.tugas;

import java.io.*;
import java.util.Arrays;
import java.util.Scanner;
import java.util.StringTokenizer;

public class Main {
    public static void main(String[] args) throws IOException {

        Scanner terminalInput = new Scanner(System.in);
        String pilihanUser;
        boolean isLanjutkan = true;

        while (isLanjutkan) {
            clearScreen();
            System.out.println("DATA MAHASISWA INBOUND PERTUKARAN PELAJAR\n");
            System.out.println("1.\tLihat seluruh data");
            System.out.println("2.\tCari data mahasiswa");
            System.out.println("3.\tBuat data baru");
            System.out.println("4.\tEdit data mahasiswa");
            System.out.println("5.\tHapus data mahasiswa");

            System.out.print("\n\nPilihan anda: ");
            pilihanUser = terminalInput.next();

            switch (pilihanUser) {
                case "1":
                    System.out.println("\n=================");
                    System.out.println("LIST MAHASISWA INBOUND PERTUKARAN PELAJAR");
                    System.out.println("=================");
                    tampilkanData();
                    break;
                case "2":
                    System.out.println("\n=========");
                    System.out.println("CARI DATA MAHASISWA");
                    System.out.println("=========");
                    cariData();
                    break;
                case "3":
                    System.out.println("\n================");
                    System.out.println("BUAT DATA BARU");
                    System.out.println("================");
                    tambahData();
                    tampilkanData();
                    break;
                case "4":
                    System.out.println("\n==============");
                    System.out.println("EDIT DATA MAHASISWA");
                    System.out.println("==============");
                    updateData();
                    break;
                case "5":
                    System.out.println("\n===============");
                    System.out.println("HAPUS DATA MAHASISWA");
                    System.out.println("===============");
                    deleteData();
                    break;
                default:
                    System.err.println("\nInput anda tidak ditemukan\nSilahkan pilih [1-5]");
            }

            isLanjutkan = getYesorNo("Apakah Anda ingin melanjutkan");
        }
    }

    private static void updateData() throws IOException {
        // kita ambil database original
        File database = new File("database.txt");
        FileReader fileInput = new FileReader(database);
        BufferedReader bufferedInput = new BufferedReader(fileInput);

        // kita buat database sementara
        File tempDB = new File("tempDB.txt");
        FileWriter fileOutput = new FileWriter(tempDB);
        BufferedWriter bufferedOutput = new BufferedWriter(fileOutput);

        // tampilkan data
        System.out.println("LIST MAHASISWA INBOUND PERTUKARAN PELAJAR");
        tampilkanData();

        // ambil user input / pilihan data
        Scanner terminalInput = new Scanner(System.in);
        System.out.print("\nMasukan No urut mahasiswa yang akan diupdate: ");
        int updateNum = terminalInput.nextInt();

        // tampilkan data yang ingin diupdate
        String data = bufferedInput.readLine();
        int entryCounts = 0;

        while (data != null) {
            entryCounts++;
            StringTokenizer st = new StringTokenizer(data, ",");
            // tampilkan entrycounts == updateNum
            if (updateNum == entryCounts) {
                System.out.println("\nData yang ingin anda update adalah:");
                System.out.println("---------------------------------------");
                System.out.println("Referensi           : " + st.nextToken());
                System.out.println("Program             : " + st.nextToken());
                System.out.println("Nama Mahasiswa      : " + st.nextToken());
                System.out.println("Asal Jurusan        : " + st.nextToken());
                System.out.println("Asal Kampus         : " + st.nextToken());

                // update data
                // mengambil input dari user
                String[] fieldData = {"program", "nama", "jurusan", "kampus"};
                String[] tempData = new String[4];

                st = new StringTokenizer(data, ",");
                String originalData = st.nextToken();

                for (int i = 0; i < fieldData.length; i++) {
                    boolean isUpdate = getYesorNo("apakah anda ingin merubah " + fieldData[i]);
                    originalData = st.nextToken();
                    if (isUpdate) {
                        //user input
                        if (fieldData[i].equalsIgnoreCase("program")) {
                            System.out.print("Masukan jenis program yang diikuti: ");
                            tempData[i] = ambilData();
                        } else {
                            terminalInput = new Scanner(System.in);
                            System.out.print("\nMasukan " + fieldData[i] + " baru: ");
                            tempData[i] = terminalInput.nextLine();
                        }
                    } else {
                        tempData[i] = originalData;
                    }
                }

                // tampilkan data baru ke layar
                st = new StringTokenizer(data, ",");
                st.nextToken();
                System.out.println("\nData baru anda adalah ");
                System.out.println("---------------------------------------");
                System.out.println("Program             : " + st.nextToken() + " --> " + tempData[0]);
                System.out.println("Nama Mahasiswa      : " + st.nextToken() + " --> " + tempData[1]);
                System.out.println("Asal Jurusan        : " + st.nextToken() + " --> " + tempData[2]);
                System.out.println("Asal Kampus         : " + st.nextToken() + " --> " + tempData[3]);

                boolean isUpdate = getYesorNo("apakah anda yakin ingin mengupdate data tersebut");

                if (isUpdate) {
                    // cek data baru di database
                    boolean isExist = cekMahasiswaDiDatabase(tempData, false);
                    if (isExist) {
                        System.err.println("Data mahasiswa sudah ada di database, proses update dibatalkan, \nsilahkan delete data yang bersangkutan");
                        // copy data
                        bufferedOutput.write(data);
                    } else {
                        // format data baru kedalam database
                        String program = tempData[0];
                        String nama = tempData[1];
                        String jurusan = tempData[2];
                        String kampus = tempData[3];

                        // kita bikin primary key
                        String programEntry = nama.replaceAll(nama, program) + 1;
                        String punulisTanpaSpasi = nama.replaceAll("\\s+", "");
                        String primaryKey = punulisTanpaSpasi + "_" + program + "_" + programEntry;

                        // tulis data ke database
                        bufferedOutput.write(primaryKey + "," + program + "," + nama + "," + jurusan + "," + kampus);
                    }
                } else {
                    // copy data
                    bufferedOutput.write(data);
                }
            } else {
                // copy data
                bufferedOutput.write(data);
            }
            bufferedOutput.newLine();
            data = bufferedInput.readLine();
        }

        // menulis data ke file
        bufferedOutput.flush();
        bufferedInput.close();
        bufferedOutput.close();
        fileInput.close();
        fileOutput.close();
        database.delete();
        tempDB.renameTo(database);
        System.gc();

// delete original file
        try {
            if (database.delete()) {
                System.out.println("*File berhasil di delete");
            } else {
                System.out.println("*Operasi gagal");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
// rename file sementara jadi original
        boolean success = tempDB.renameTo(database);
        if (!success) {
            System.out.println("*Rename data sementra gagal");
        } else {
            System.out.println("*Rename data sementara berhasil");
        }
    }

    private static void deleteData() throws  IOException{
        // kita ambil database original
        File database = new File("database.txt");
        FileReader fileInput = new FileReader(database);
        BufferedReader bufferedInput = new BufferedReader(fileInput);

        // kita buat database sementara
        File tempDB = new File("tempDB.txt");
        FileWriter fileOutput = new FileWriter(tempDB);
        BufferedWriter bufferedOutput = new BufferedWriter(fileOutput);

        // tampilkan data
        System.out.println("DAFTAR MAHASISWA");
        tampilkanData();

        // kita ambil user input untuk mendelete data
        Scanner terminalInput = new Scanner(System.in);
        System.out.print("\nMasukan No urut data mahasiswa yang akan dihapus: ");
        int deleteNum = terminalInput.nextInt();

        // looping untuk membaca tiap data baris dan skip data yang akan didelete
        boolean isFound = false;
        int entryCounts = 0;

        String data = bufferedInput.readLine();

        while (data != null){
            entryCounts++;
            boolean isDelete = false;

            StringTokenizer st = new StringTokenizer(data,",");

            // tampilkan data yang ingin di hapus
            if (deleteNum == entryCounts){
                System.out.println("\nData yang ingin anda hapus adalah:");
                System.out.println("-----------------------------------");
                System.out.println("Referensi       : " + st.nextToken());
                System.out.println("Program         : " + st.nextToken());
                System.out.println("Nama Mahasiswa  : " + st.nextToken());
                System.out.println("Asal Jurusan    : " + st.nextToken());
                System.out.println("Asal Kampus     : " + st.nextToken());

                isDelete = getYesorNo("Apakah anda yakin akan menghapus?");
                isFound = true;
            }

            if(isDelete){
                //skip pindahkan data dari original ke sementara
                System.out.println("Data berhasil dihapus");
            } else {
                // kita pindahkan data dari original ke sementara
                bufferedOutput.write(data);
                bufferedOutput.newLine();
            }
            data = bufferedInput.readLine();
        }

        if(!isFound){
            System.err.println("Data mahasiswa tidak ditemukan");
        }

        // menulis data ke file
        bufferedOutput.flush();
        bufferedInput.close();
        bufferedOutput.close();
        fileInput.close();
        fileOutput.close();
        database.delete();
        tempDB.renameTo(database);
        System.gc();

// delete original file
        try {
            if (database.delete()) {
                System.out.println("*File berhasil di delete");
            } else {
                System.out.println("*Operasi gagal");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
// rename file sementara jadi original
        boolean success = tempDB.renameTo(database);
        if (!success) {
            System.out.println("*Rename data sementra gagal");
        } else {
            System.out.println("*Rename data sementara berhasil");
        }
    }

    private static void tampilkanData() throws IOException{

        FileReader fileInput;
        BufferedReader bufferInput;

        try {
            fileInput = new FileReader("database.txt");
            bufferInput = new BufferedReader(fileInput);
        } catch (Exception e){
            System.err.println("Database Tidak ditemukan");
            System.err.println("Silahkan tambah data");
            tambahData();
            return;
        }

        System.out.println("\n| No |\tProgram                         |\tNama Mahasiswa                   |\tAsal Jurusan                     |\tAsal Kampus");
        System.out.println("-------------------------------------------------------------------------------------------------------------------------------------------------------------------------");

        String data = bufferInput.readLine();
        int nomorData = 0;
        while(data != null) {
            nomorData++;

            StringTokenizer stringToken = new StringTokenizer(data, ",");

            stringToken.nextToken();
            System.out.printf("| %2d ", nomorData);
            System.out.printf("|\t%-30s  ", stringToken.nextToken());
            System.out.printf("|\t%-30s   ", stringToken.nextToken());
            System.out.printf("|\t%-30s   ", stringToken.nextToken());
            System.out.printf("|\t%s   ", stringToken.nextToken());
            System.out.print("\n");

            data = bufferInput.readLine();
        }

        System.out.println("-------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
    }

    private static void cariData() throws IOException{

        // membaca database ada atau tidak
        try {
            File file = new File("database.txt");
        } catch (Exception e){
            System.err.println("Database Tidak ditemukan");
            System.err.println("Silahkan tambah data");
            tambahData();
            return;
        }

        // kita ambil keyword dari user
        Scanner terminalInput = new Scanner(System.in);
        System.out.print("Masukan kata kunci untuk mencari mahasiswa: ");
        String cariString = terminalInput.nextLine();
        String[] keywords = cariString.split("\\s+");

        // kita cek keyword di database
        cekMahasiswaDiDatabase(keywords,true);

    }

    private static void tambahData() throws IOException{


        FileWriter fileOutput = new FileWriter("database.txt",true);
        BufferedWriter bufferOutput = new BufferedWriter(fileOutput);

        // mengambil input dari user
        Scanner terminalInput = new Scanner(System.in);
        String nama, kampus, jurusan, program;

        System.out.print("masukan nama mahasiswa: ");
        nama = terminalInput.nextLine();
        System.out.print("masukan kampus asal mahasiswa: ");
        kampus = terminalInput.nextLine();
        System.out.print("masukan jurusan asal mahasiswa: ");
        jurusan = terminalInput.nextLine();
        System.out.print("masukan program mahasiswa: ");
        program = terminalInput.nextLine();

        // cek buku di database
        String[] keywords = {program+","+nama+","+jurusan+","+kampus};
        System.out.println(Arrays.toString(keywords));
        boolean isExist = cekMahasiswaDiDatabase(keywords,false);
        // menulis buku di databse
        if (!isExist){
            System.out.println(ambilEntryPerProgram(nama, program));
            long programEntry = ambilEntryPerProgram(nama, program) + 1;
            String punulisTanpaSpasi = nama.replaceAll("\\s+","");
            String primaryKey = punulisTanpaSpasi+"_"+program+"_"+programEntry;
            System.out.println("\nData yang akan anda masukan adalah");
            System.out.println("----------------------------------------");
            System.out.println("PrimaryKey        : " + primaryKey);
            System.out.println("Program Mahasiswa : " + program);
            System.out.println("Nama Mahasiswa    : " + nama);
            System.out.println("Asal Kampus       : " + kampus);
            System.out.println("Asal Jurusan      : " + jurusan);

            boolean isTambah = getYesorNo("Apakah akan ingin menambah data tersebut? ");
            if(isTambah){
                bufferOutput.write(primaryKey + "," + program + ","+ nama +"," + jurusan + ","+kampus);
                bufferOutput.newLine();
                bufferOutput.flush();
            }

        } else {
            System.out.println("data yang anda akan masukan sudah tersedia di database dengan data berikut:");
            cekMahasiswaDiDatabase(keywords,true);
        }
        bufferOutput.close();
    }

    private static long ambilEntryPerProgram(String nama, String program) throws IOException {
        FileReader fileInput = new FileReader("database.txt");
        BufferedReader bufferInput = new BufferedReader(fileInput);

        long entry = 0;
        String data = bufferInput.readLine();
        Scanner dataScanner;
        String primaryKey;

        while(data != null){
            dataScanner = new Scanner(data);
            dataScanner.useDelimiter(",");
            primaryKey = dataScanner.next();
            dataScanner = new Scanner(primaryKey);
            dataScanner.useDelimiter("_");

            nama = nama.replaceAll("\\s+","");

            if (nama.equalsIgnoreCase(dataScanner.next()) && program.equalsIgnoreCase(dataScanner.next()) ) {
                entry = dataScanner.nextInt();
            }

            data = bufferInput.readLine();
        }

        return entry;
    }

    private static boolean cekMahasiswaDiDatabase(String[] keywords, boolean isDisplay) throws IOException{

        FileReader fileInput = new FileReader("database.txt");
        BufferedReader bufferInput = new BufferedReader(fileInput);

        String data = bufferInput.readLine();
        boolean isExist = false;
        int nomorData = 0;

        if (isDisplay) {
            System.out.println("\n| No |\tProgram                         |\tNama Mahasiswa                   |\tAsal Jurusan                     |\tAsal Kampus");
            System.out.println("-------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        }

        while(data != null){

            // cek keywords didalam baris
            isExist = true;

            for(String keyword:keywords){
                isExist = isExist && data.toLowerCase().contains(keyword.toLowerCase());
            }

            // jika keywordsnya cocok maka tampilkan

            if(isExist){
                if(isDisplay) {
                    nomorData++;
                    StringTokenizer stringToken = new StringTokenizer(data, ",");

                    stringToken.nextToken();
                    System.out.printf("| %2d ", nomorData);
                    System.out.printf("|\t%-30s  ", stringToken.nextToken());
                    System.out.printf("|\t%-30s   ", stringToken.nextToken());
                    System.out.printf("|\t%-30s   ", stringToken.nextToken());
                    System.out.printf("|\t%s   ", stringToken.nextToken());
                    System.out.print("\n");
                } else {
                    break;
                }
            }

            data = bufferInput.readLine();
        }

        if (isDisplay){
            System.out.println("----------------------------------------------------------------------------------------------------------");
        }

        return isExist;
    }

    private static boolean getYesorNo(String message){
        Scanner terminalInput = new Scanner(System.in);
        System.out.print("\n"+message+" (y/n)? ");
        String pilihanUser = terminalInput.next();

        while(!pilihanUser.equalsIgnoreCase("y") && !pilihanUser.equalsIgnoreCase("n")) {
            System.err.println("Pilihan anda y atau n");
            System.out.print("\n"+message+" (y/n)? ");
            pilihanUser = terminalInput.next();
        }

        return pilihanUser.equalsIgnoreCase("y");

    }

    private static void clearScreen(){
        try {
            if (System.getProperty("os.name").contains("Windows")){
                new ProcessBuilder("cmd","/c","cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033\143");
            }
        } catch (Exception ex){
            System.err.println("tidak bisa clear screen");
        }
    }

    private static String ambilData() throws IOException{
        Scanner terminalInput = new Scanner(System.in);
        String dataInput = terminalInput.nextLine();
        return dataInput;
    }

}

