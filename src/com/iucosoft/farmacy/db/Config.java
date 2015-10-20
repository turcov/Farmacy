/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iucosoft.farmacy.db;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ольга
 */
public class Config {
    private static final Logger LOG = Logger.getLogger(Config.class.getName());
    final static String fileName="farmacy.properties";
    public static Properties loadProperties(){
        Properties props=new Properties();
        try {
            FileInputStream fis=new FileInputStream(fileName);
            props.load(fis);
            fis.close();
        } catch (FileNotFoundException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
        return props;
    }

    public static void saveProperties(Properties props) {
        
        try {
            FileOutputStream fos=new FileOutputStream(fileName);
            props.store(fos, "AAA");
            fos.close();
            LOG.info("properties saved succesfully");
        } catch (FileNotFoundException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Config.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
        
}
