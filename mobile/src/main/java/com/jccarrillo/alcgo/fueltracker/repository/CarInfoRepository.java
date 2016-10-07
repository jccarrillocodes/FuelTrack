package com.jccarrillo.alcgo.fueltracker.repository;

import android.util.Log;

import com.jccarrillo.alcgo.fueltracker.domain.CarInfo;
import com.jccarrillo.alcgo.fueltracker.util.ContextManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InterruptedIOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Juan Carlos on 04/10/2016.
 */

public final class CarInfoRepository {

    private static String getRepositoryPath(){
        return ContextManager.getInstance().getContext().getFilesDir().toString() + "/cars/";
    }

    public static CarInfo loadObject(String name) {
        File suspend_f = new File(getRepositoryPath(), name);

        CarInfo carInfo = null;
        FileInputStream fis = null;
        ObjectInputStream is = null;

        try {
            fis = new FileInputStream(suspend_f);
            is = new ObjectInputStream(fis);
            carInfo = (CarInfo)is.readObject();
        } catch (Exception e) {
            // String val = e.getMessage();
            Log.e(CarInfoRepository.class.toString(), "No se ha podido cargar el CarInfo de la store.");
            return null;
        } finally {
            try {
                if (fis != null)
                    fis.close();
                if (is != null)
                    is.close();
            } catch (Exception e) {
                Log.e(CarInfoRepository.class.toString(), "No se ha podido cargar el CarInfo de la store.");
                return null;
            }
        }

        return carInfo;
    }

    private static boolean writeObject( String filename, CarInfo carInfo ){
        File suspend_f = new File( getRepositoryPath(), filename);

        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        boolean keep = true;

        try {
            fos = new FileOutputStream(suspend_f);
            oos = new ObjectOutputStream(fos);
            oos.writeObject( carInfo );
        } catch (Exception e) {
            keep = false;
            Log.e(CarInfoRepository.class.toString(), "No se ha podido guardar el CarInfo en la store.");
        } finally {
            try {
                if (oos != null)
                    oos.close();
                if (fos != null)
                    fos.close();
                if (keep == false)
                    suspend_f.delete();
            } catch (Exception e) { /* do nothing */
                keep = false;
                Log.e(CarInfoRepository.class.toString(), "No se ha podido guardar el CarInfo en la store.");
            }
        }

        return keep;
    }

    public static List<Integer> getListOfCarInfo(){
        List<Integer> result = new ArrayList<>();

        File f = new File( getRepositoryPath() );
        f.mkdirs();

        File files[] = f.listFiles();
        for( File file: files )
            if( !file.isDirectory() ){
                try{
                    Integer uid = Integer.parseInt( file.getName() );
                    result.add( uid );
                }catch (Exception ex ){
                    Log.e(CarInfoRepository.class.toString(), "No se pudo parsear a entero " + file.getName() );
                }
            }

        return result;
    }

    public static CarInfo getCarInfo( int uid ){
        File f = new File( getRepositoryPath() );
        f.mkdirs();

        return loadObject( String.valueOf( uid ) );
    }

    public static boolean setCarInfo( CarInfo carInfo ){
        File f = new File( getRepositoryPath() );
        f.mkdirs();

        if( carInfo.getUid() <= 0 )
            for( int i = 0; i < 10000; ++i ){
                f = new File( getRepositoryPath(), String.valueOf( i ) );
                if( !f.exists() ){
                    carInfo.setUid( i );
                    break;
                }
            }

        if( carInfo.getUid() <= 0 )
            return false;

        return writeObject( String.valueOf( carInfo.getUid() ), carInfo );
    }
}
