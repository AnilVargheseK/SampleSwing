/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.company.swing_0.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 *
 * @author Aghil
 */
public class PropertiesLoader {

    private static final Properties properties = new Properties();

    private PropertiesLoader() {
    }

    public static void initialise() throws IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream propertiesInputStream = null;
        InputStream dbPropertiesInputStream = null;
        try {
            propertiesInputStream = classLoader.getResourceAsStream("notis_properties.properties");
            dbPropertiesInputStream = classLoader.getResourceAsStream("db_properties.properties");

            properties.load(propertiesInputStream);
            properties.load(dbPropertiesInputStream);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (propertiesInputStream != null) {
                propertiesInputStream.close();
            }
            if(dbPropertiesInputStream != null) {
                dbPropertiesInputStream.close();
            }
        }
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }
}
