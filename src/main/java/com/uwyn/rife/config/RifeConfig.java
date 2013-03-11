/*
 * Copyright 2001-2013 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 */
package com.uwyn.rife.config;

import com.uwyn.rife.config.exceptions.DateFormatInitializationException;
import com.uwyn.rife.tools.Convert;
import com.uwyn.rife.tools.Localization;
import com.uwyn.rife.tools.StringUtils;
import com.uwyn.rife.tools.exceptions.LightweightError;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public abstract class RifeConfig
{
    public static abstract class Global
    {
        public static final String PARAM_TEMP_PATH = "TEMP_PATH";
        public static final String PARAM_APPLICATION_CLASSPATH = "APPLICATION_CLASSPATH";
        public static final String PARAM_JAVA_COMPILER_PATH = "JAVA_COMPILER_PATH";
        public static final String PARAM_JAVA_COMPILER_ARGS = "JAVA_COMPILER_ARGS";
        public static final String PARAM_JAVA_COMPILER_INTERNAL = "JAVA_COMPILER_INTERNAL";
        public static final String PARAM_AUTO_RELOAD_DELAY = "AUTO_RELOAD_DELAY";
        public static final String PARAM_USE_FAST_EXCEPTIONS = "USE_FAST_EXCEPTIONS";
        private static final String DEFAULT_JAVA_COMPILER_PATH;

        static
        {
            if (System.getProperty("os.name").toLowerCase().contains("windows"))
            {
                DEFAULT_JAVA_COMPILER_PATH = "javac.exe";
            }
            else
            {
                DEFAULT_JAVA_COMPILER_PATH = "javac";
            }
        }

        private static final boolean DEFAULT_JAVA_COMPILER_INTERNAL = false;
        private static final int DEFAULT_AUTO_RELOAD_DELAY = 10 * 1000;
        private static String sFallbackTempPath;

        static
        {
            String tmpdir = System.getProperty("java.io.tmpdir");
            sFallbackTempPath = StringUtils.stripFromEnd(tmpdir, File.separator);
        }

        private static String sFallbackApplicationClassPath = "";
        private static int sAutoReloadDelay = DEFAULT_AUTO_RELOAD_DELAY;

        public static String getTempPath()
        {
            if (Config.hasRepInstance())
            {
                return Config.getRepInstance().getString(PARAM_TEMP_PATH, sFallbackTempPath);
            }

            return sFallbackTempPath;
        }

        public static synchronized void setTempPath(String path)
        {
            if (null == path)
            {
                throw new IllegalArgumentException("path can't be null.");
            }
            if (0 == path.length())
            {
                throw new IllegalArgumentException("path can't be empty.");
            }

            if (Config.hasRepInstance())
            {
                Config.getRepInstance().setParameter(PARAM_TEMP_PATH, path);
            }
            else
            {
                sFallbackTempPath = path;
            }
        }

        public static String getApplicationClassPath()
        {
            if (Config.hasRepInstance())
            {
                return Config.getRepInstance().getString(PARAM_APPLICATION_CLASSPATH, sFallbackApplicationClassPath);
            }

            return sFallbackApplicationClassPath;
        }

        public static synchronized void setApplicationClassPath(String path)
        {
            if (null == path)
            {
                throw new IllegalArgumentException("path can't be null.");
            }
            if (0 == path.length())
            {
                throw new IllegalArgumentException("path can't be empty.");
            }

            if (Config.hasRepInstance())
            {
                Config.getRepInstance().setParameter(PARAM_APPLICATION_CLASSPATH, path);
            }
            else
            {
                sFallbackApplicationClassPath = path;
            }
        }

        public static String getJavaCompilerPath()
        {
            if (Config.hasRepInstance())
            {
                return Config.getRepInstance().getString(PARAM_JAVA_COMPILER_PATH, DEFAULT_JAVA_COMPILER_PATH);
            }

            return DEFAULT_JAVA_COMPILER_PATH;
        }

        public static boolean isJavaCompilerPathSet()
        {
            return Config.hasRepInstance() && Config.getRepInstance().hasParameter(PARAM_JAVA_COMPILER_PATH);

        }

        public static boolean getJavaCompilerInternal()
        {
            if (Config.hasRepInstance())
            {
                return Config.getRepInstance().getBool(PARAM_JAVA_COMPILER_INTERNAL, DEFAULT_JAVA_COMPILER_INTERNAL);
            }

            return DEFAULT_JAVA_COMPILER_INTERNAL;
        }

        public static boolean areJavaCompilerArgsSet()
        {
            return Config.hasRepInstance() && Config.getRepInstance().hasList(PARAM_JAVA_COMPILER_ARGS);

        }

        public static Collection<String> getJavaCompilerArgs()
        {
            if (Config.hasRepInstance())
            {
                return Config.getRepInstance().getStringItems(PARAM_JAVA_COMPILER_ARGS);
            }

            return null;
        }

        public static boolean isInternalJavaCompilerAvailable()
        {
            try
            {
                Class klass = Class.forName("com.sun.tools.javac.Main");
                if (null == klass)
                {
                    return false;
                }
            }
            catch (ClassNotFoundException e)
            {
                return false;
            }

            return true;
        }

        public static int getAutoReloadDelay()
        {
            if (Config.hasRepInstance())
            {
                return Config.getRepInstance().getInt(PARAM_AUTO_RELOAD_DELAY, sAutoReloadDelay);
            }

            return sAutoReloadDelay;
        }

        public static synchronized void setAutoReloadDelay(int delay)
        {
            if (Config.hasRepInstance())
            {
                Config.getRepInstance().setParameter(PARAM_AUTO_RELOAD_DELAY, delay);
            }
            else
            {
                sAutoReloadDelay = delay;
            }
        }

        public static boolean getUseFastExceptions()
        {
            if (Config.hasRepInstance())
            {
                return Config.getRepInstance().getBool(PARAM_USE_FAST_EXCEPTIONS, LightweightError.getUseFastExceptions());
            }

            return LightweightError.getUseFastExceptions();
        }

        public static synchronized void setUseFastExceptions(boolean toggle)
        {
            if (Config.hasRepInstance())
            {
                Config.getRepInstance().setParameter(PARAM_USE_FAST_EXCEPTIONS, toggle);
            }
            else
            {
                LightweightError.setUseFastExceptions(toggle);
            }
        }
    }

    public static abstract class Authentication
    {
        public static final String PARAM_SESSION_DURATION = "SESSION_DURATION";
        public static final String PARAM_SESSION_PURGE_FREQUENCY = "SESSION_PURGE_FREQUENCY";
        public static final String PARAM_SESSION_PURGE_SCALE = "SESSION_PURGE_SCALE";
        public static final String PARAM_SESSION_RESTRICT_HOSTIP = "SESSION_RESTRICT_HOSTIP";
        public static final String PARAM_REMEMBER_DURATION = "REMEMBER_DURATION";
        public static final String PARAM_REMEMBER_PURGE_FREQUENCY = "REMEMBER_PURGE_FREQUENCY";
        public static final String PARAM_REMEMBER_PURGE_SCALE = "REMEMBER_PURGE_SCALE";
        public static final String PARAM_TABLE_ROLE = "TABLE_ROLE";
        public static final String PARAM_SEQUENCE_ROLE = "SEQUENCE_ROLE";
        public static final String PARAM_TABLE_USER = "TABLE_USER";
        public static final String PARAM_TABLE_ROLELINK = "TABLE_ROLELINK";
        public static final String PARAM_TABLE_AUTHENTICATION = "TABLE_AUTHENTICATION";
        public static final String PARAM_TABLE_REMEMBER = "TABLE_REMEMBER";
        public static final String PARAM_ROLE_NAME_MAXIMUM_LENGTH = "ROLE_NAME_MAXIMUM_LENGTH";
        public static final String PARAM_LOGIN_MAXIMUM_LENGTH = "LOGIN_NAME_MAXIMUM_LENGTH";
        public static final String PARAM_LOGIN_MINIMUM_LENGTH = "LOGIN_NAME_MINIMUM_LENGTH";
        public static final String PARAM_PASSWORD_MAXIMUM_LENGTH = "PASSWORD_MAXIMUM_LENGTH";
        public static final String PARAM_PASSWORD_MINIMUM_LENGTH = "PASSWORD_MINIMUM_LENGTH";
        private static final long DEFAULT_SESSION_DURATION = 1000 * 60 * 20;                         // 20 minutes
        private static final int DEFAULT_SESSION_PURGE_FREQUENCY = 20;                               // 20 out of 1000 times, means 1/50th of the time
        private static final int DEFAULT_SESSION_PURGE_SCALE = 1000;
        private static final boolean DEFAULT_SESSION_RESTRICT_HOSTIP = false;
        private static final long DEFAULT_REMEMBER_DURATION = 1000L * 60L * 60L * 24L * 30L * 3L;    // 3 months
        private static final int DEFAULT_REMEMBER_PURGE_FREQUENCY = 20;                              // 20 out of 1000 times, means 1/50th of the time
        private static final int DEFAULT_REMEMBER_PURGE_SCALE = 1000;
        private static final int DEFAULT_ROLE_NAME_MAXIMUM_LENGTH = 20;
        private static final int DEFAULT_LOGIN_MAXIMUM_LENGTH = 20;
        private static final int DEFAULT_LOGIN_MINIMUM_LENGTH = 5;
        private static final int DEFAULT_PASSWORD_MAXIMUM_LENGTH = 100;
        private static final int DEFAULT_PASSWORD_MINIMUM_LENGTH = 5;
        private static final String DEFAULT_TABLE_ROLE = "AuthRole";
        private static final String DEFAULT_SEQUENCE_ROLE = "SEQ_AUTHROLE";
        private static final String DEFAULT_TABLE_USER = "AuthUser";
        private static final String DEFAULT_TABLE_ROLELINK = "AuthRoleLink";
        private static final String DEFAULT_TABLE_AUTHENTICATION = "Authentication";
        private static final String DEFAULT_TABLE_REMEMBER = "AuthRemember";

        public static int getPasswordMinimumLength()
        {
            if (Config.hasRepInstance())
            {
                return Config.getRepInstance().getInt(PARAM_PASSWORD_MINIMUM_LENGTH, DEFAULT_PASSWORD_MINIMUM_LENGTH);
            }

            return DEFAULT_PASSWORD_MINIMUM_LENGTH;
        }

        public static int getPasswordMaximumLength()
        {
            if (Config.hasRepInstance())
            {
                return Config.getRepInstance().getInt(PARAM_PASSWORD_MAXIMUM_LENGTH, DEFAULT_PASSWORD_MAXIMUM_LENGTH);
            }

            return DEFAULT_PASSWORD_MAXIMUM_LENGTH;
        }

        public static int getLoginMinimumLength()
        {
            if (Config.hasRepInstance())
            {
                return Config.getRepInstance().getInt(PARAM_LOGIN_MINIMUM_LENGTH, DEFAULT_LOGIN_MINIMUM_LENGTH);
            }

            return DEFAULT_LOGIN_MINIMUM_LENGTH;
        }

        public static int getLoginMaximumLength()
        {
            if (Config.hasRepInstance())
            {
                return Config.getRepInstance().getInt(PARAM_LOGIN_MAXIMUM_LENGTH, DEFAULT_LOGIN_MAXIMUM_LENGTH);
            }

            return DEFAULT_LOGIN_MAXIMUM_LENGTH;
        }

        public static int getRoleNameMaximumLength()
        {
            if (Config.hasRepInstance())
            {
                return Config.getRepInstance().getInt(PARAM_ROLE_NAME_MAXIMUM_LENGTH, DEFAULT_ROLE_NAME_MAXIMUM_LENGTH);
            }

            return DEFAULT_ROLE_NAME_MAXIMUM_LENGTH;
        }

        public static long getSessionDuration()
        {
            if (Config.hasRepInstance())
            {
                return Config.getRepInstance().getLong(PARAM_SESSION_DURATION, DEFAULT_SESSION_DURATION);
            }

            return DEFAULT_SESSION_DURATION;
        }

        public static int getSessionPurgeFrequency()
        {
            if (Config.hasRepInstance())
            {
                return Config.getRepInstance().getInt(PARAM_SESSION_PURGE_FREQUENCY, DEFAULT_SESSION_PURGE_FREQUENCY);
            }

            return DEFAULT_SESSION_PURGE_FREQUENCY;
        }

        public static int getSessionPurgeScale()
        {
            if (Config.hasRepInstance())
            {
                return Config.getRepInstance().getInt(PARAM_SESSION_PURGE_SCALE, DEFAULT_SESSION_PURGE_SCALE);
            }

            return DEFAULT_SESSION_PURGE_SCALE;
        }

        public static boolean getSessionRestrictHostIp()
        {
            if (Config.hasRepInstance())
            {
                return Config.getRepInstance().getBool(PARAM_SESSION_RESTRICT_HOSTIP, DEFAULT_SESSION_RESTRICT_HOSTIP);
            }

            return DEFAULT_SESSION_RESTRICT_HOSTIP;
        }

        public static long getRememberDuration()
        {
            if (Config.hasRepInstance())
            {
                return Config.getRepInstance().getLong(PARAM_REMEMBER_DURATION, DEFAULT_REMEMBER_DURATION);
            }

            return DEFAULT_REMEMBER_DURATION;
        }

        public static int getRememberPurgeFrequency()
        {
            if (Config.hasRepInstance())
            {
                return Config.getRepInstance().getInt(PARAM_REMEMBER_PURGE_FREQUENCY, DEFAULT_REMEMBER_PURGE_FREQUENCY);
            }

            return DEFAULT_REMEMBER_PURGE_FREQUENCY;
        }

        public static int getRememberPurgeScale()
        {
            if (Config.hasRepInstance())
            {
                return Config.getRepInstance().getInt(PARAM_REMEMBER_PURGE_SCALE, DEFAULT_REMEMBER_PURGE_SCALE);
            }

            return DEFAULT_REMEMBER_PURGE_SCALE;
        }

        public static String getTableRole()
        {
            if (Config.hasRepInstance())
            {
                return Config.getRepInstance().getString(PARAM_TABLE_ROLE, DEFAULT_TABLE_ROLE);
            }

            return DEFAULT_TABLE_ROLE;
        }

        public static String getSequenceRole()
        {
            if (Config.hasRepInstance())
            {
                return Config.getRepInstance().getString(PARAM_SEQUENCE_ROLE, DEFAULT_SEQUENCE_ROLE);
            }

            return DEFAULT_SEQUENCE_ROLE;
        }

        public static String getTableUser()
        {
            if (Config.hasRepInstance())
            {
                return Config.getRepInstance().getString(PARAM_TABLE_USER, DEFAULT_TABLE_USER);
            }

            return DEFAULT_TABLE_USER;
        }

        public static String getTableRoleLink()
        {
            if (Config.hasRepInstance())
            {
                return Config.getRepInstance().getString(PARAM_TABLE_ROLELINK, DEFAULT_TABLE_ROLELINK);
            }

            return DEFAULT_TABLE_ROLELINK;
        }

        public static String getTableAuthentication()
        {
            if (Config.hasRepInstance())
            {
                return Config.getRepInstance().getString(PARAM_TABLE_AUTHENTICATION, DEFAULT_TABLE_AUTHENTICATION);
            }

            return DEFAULT_TABLE_AUTHENTICATION;
        }

        public static String getTableRemember()
        {
            if (Config.hasRepInstance())
            {
                return Config.getRepInstance().getString(PARAM_TABLE_REMEMBER, DEFAULT_TABLE_REMEMBER);
            }

            return DEFAULT_TABLE_REMEMBER;
        }
    }

    public static abstract class Cmf
    {
        public static final String PARAM_SEQUENCE_CONTENTREPOSITORY = "SEQUENCE_CONTENTREPOSITORY";
        public static final String PARAM_SEQUENCE_CONTENTINFO = "SEQUENCE_CONTENTINFO";
        public static final String PARAM_TABLE_CONTENTREPOSITORY = "TABLE_CONTENTREPOSITORY";
        public static final String PARAM_TABLE_CONTENTINFO = "TABLE_CONTENTINFO";
        public static final String PARAM_TABLE_CONTENTATTRIBUTE = "TABLE_CONTENTATTRIBUTE";
        public static final String PARAM_TABLE_CONTENTPROPERTY = "TABLE_CONTENTPROPERTY";
        public static final String PARAM_TABLE_CONTENTSTOREIMAGE = "TABLE_CONTENTSTOREIMAGE";
        public static final String PARAM_TABLE_CONTENTSTORETEXT = "TABLE_CONTENTSTORETEXT";
        public static final String PARAM_TABLE_CONTENTSTORERAWINFO = "TABLE_CONTENTSTORERAWINFO";
        public static final String PARAM_TABLE_CONTENTSTORERAWCHUNK = "TABLE_CONTENTSTORERAWCHUNK";
        private static final String DEFAULT_SEQUENCE_CONTENTREPOSITORY = "SEQ_CONTENTREPOSITORY";
        private static final String DEFAULT_SEQUENCE_CONTENTINFO = "SEQ_CONTENTINFO";
        private static final String DEFAULT_TABLE_CONTENTREPOSITORY = "ContentRepository";
        private static final String DEFAULT_TABLE_CONTENTINFO = "ContentInfo";
        private static final String DEFAULT_TABLE_CONTENTATTRIBUTE = "ContentAttribute";
        private static final String DEFAULT_TABLE_CONTENTPROPERTY = "ContentProperty";
        private static final String DEFAULT_TABLE_CONTENTSTOREIMAGE = "ContentStoreImage";
        private static final String DEFAULT_TABLE_CONTENTSTORETEXT = "ContentStoreText";
        private static final String DEFAULT_TABLE_CONTENTSTORERAWINFO = "ContentStoreRawInfo";
        private static final String DEFAULT_TABLE_CONTENTSTORERAWCHUNK = "ContentStoreRawChunk";

        public static String getSequenceContentRepository()
        {
            if (Config.hasRepInstance())
            {
                return Config.getRepInstance().getString(PARAM_SEQUENCE_CONTENTREPOSITORY, DEFAULT_SEQUENCE_CONTENTREPOSITORY);
            }

            return DEFAULT_SEQUENCE_CONTENTREPOSITORY;
        }

        public static String getSequenceContentInfo()
        {
            if (Config.hasRepInstance())
            {
                return Config.getRepInstance().getString(PARAM_SEQUENCE_CONTENTINFO, DEFAULT_SEQUENCE_CONTENTINFO);
            }

            return DEFAULT_SEQUENCE_CONTENTINFO;
        }

        public static String getTableContentRepository()
        {
            if (Config.hasRepInstance())
            {
                return Config.getRepInstance().getString(PARAM_TABLE_CONTENTREPOSITORY, DEFAULT_TABLE_CONTENTREPOSITORY);
            }

            return DEFAULT_TABLE_CONTENTREPOSITORY;
        }

        public static String getTableContentInfo()
        {
            if (Config.hasRepInstance())
            {
                return Config.getRepInstance().getString(PARAM_TABLE_CONTENTINFO, DEFAULT_TABLE_CONTENTINFO);
            }

            return DEFAULT_TABLE_CONTENTINFO;
        }

        public static String getTableContentAttribute()
        {
            if (Config.hasRepInstance())
            {
                return Config.getRepInstance().getString(PARAM_TABLE_CONTENTATTRIBUTE, DEFAULT_TABLE_CONTENTATTRIBUTE);
            }

            return DEFAULT_TABLE_CONTENTATTRIBUTE;
        }

        public static String getTableContentProperty()
        {
            if (Config.hasRepInstance())
            {
                return Config.getRepInstance().getString(PARAM_TABLE_CONTENTPROPERTY, DEFAULT_TABLE_CONTENTPROPERTY);
            }

            return DEFAULT_TABLE_CONTENTPROPERTY;
        }

        public static String getTableContentStoreImage()
        {
            if (Config.hasRepInstance())
            {
                return Config.getRepInstance().getString(PARAM_TABLE_CONTENTSTOREIMAGE, DEFAULT_TABLE_CONTENTSTOREIMAGE);
            }

            return DEFAULT_TABLE_CONTENTSTOREIMAGE;
        }

        public static String getTableContentStoreText()
        {
            if (Config.hasRepInstance())
            {
                return Config.getRepInstance().getString(PARAM_TABLE_CONTENTSTORETEXT, DEFAULT_TABLE_CONTENTSTORETEXT);
            }

            return DEFAULT_TABLE_CONTENTSTORETEXT;
        }

        public static String getTableContentStoreRawInfo()
        {
            if (Config.hasRepInstance())
            {
                return Config.getRepInstance().getString(PARAM_TABLE_CONTENTSTORERAWINFO, DEFAULT_TABLE_CONTENTSTORERAWINFO);
            }

            return DEFAULT_TABLE_CONTENTSTORERAWINFO;
        }

        public static String getTableContentStoreRawChunk()
        {
            if (Config.hasRepInstance())
            {
                return Config.getRepInstance().getString(PARAM_TABLE_CONTENTSTORERAWCHUNK, DEFAULT_TABLE_CONTENTSTORERAWCHUNK);
            }

            return DEFAULT_TABLE_CONTENTSTORERAWCHUNK;
        }
    }

    public static abstract class Database
    {
        public static final String PARAM_TRANSACTION_TIMEOUT = "TRANSACTION_TIMEOUT";
        public static final String PARAM_SQL_DEBUG_TRACE = "SQL_DEBUG_TRACE";
        private static final int DEFAULT_TRANSACTION_TIMEOUT = 0;    // 0 seconds : turned off
        private static final boolean DEFAULT_SQL_DEBUG_TRACE = false;

        public static int getTransactionTimeout()
        {
            if (Config.hasRepInstance())
            {
                return Config.getRepInstance().getInt(PARAM_TRANSACTION_TIMEOUT, DEFAULT_TRANSACTION_TIMEOUT);
            }

            return DEFAULT_TRANSACTION_TIMEOUT;
        }

        public static boolean getSqlDebugTrace()
        {
            if (Config.hasRepInstance())
            {
                return Config.getRepInstance().getBool(PARAM_SQL_DEBUG_TRACE, DEFAULT_SQL_DEBUG_TRACE);
            }

            return DEFAULT_SQL_DEBUG_TRACE;
        }
    }

    public static abstract class Engine
    {
        public static final String PARAM_DEFAULT_CONTENT_TYPE = "DEFAULT_CONTENT_TYPE";
        public static final String PARAM_ELEMENT_AUTO_RELOAD = "ELEMENT_AUTO_RELOAD";
        public static final String PARAM_ELEMENT_GENERATION_PATH = "ELEMENT_GENERATION_PATH";
        public static final String PARAM_ELEMENT_DEBUG_TRACE = "ELEMENT_DEBUG_TRACE";
        public static final String PARAM_ELEMENT_DEBUG_MEMORY = "ELEMENT_DEBUG_MEMORY";
        public static final String PARAM_LOG_ENGINE_EXCEPTIONS = "PRETTY_LOG_EXCEPTIONS";
        public static final String PARAM_PRETTY_ENGINE_EXCEPTIONS = "PRETTY_ENGINE_EXCEPTIONS";
        public static final String PARAM_FILEUPLOAD_PATH = "FILEUPLOAD_PATH";
        public static final String PARAM_FILEUPLOAD_SIZE_LIMIT = "FILEUPLOAD_SIZE_LIMIT";
        public static final String PARAM_FILEUPLOAD_SIZE_CHECK = "FILEUPLOAD_SIZE_CHECK";
        public static final String PARAM_FILEUPLOAD_SIZE_EXCEPTION = "FILEUPLOAD_SIZE_EXCEPTION";
        public static final String PARAM_SITE_AUTO_RELOAD = "SITE_AUTO_RELOAD";
        public static final String PARAM_CONTINUATION_DURATION = "CONTINUATION_DURATION";
        public static final String PARAM_CONTINUATION_PURGE_FREQUENCY = "CONTINUATION_PURGE_FREQUENCY";
        public static final String PARAM_CONTINUATION_PURGE_SCALE = "CONTINUATION_PURGE_SCALE";
        public static final String PARAM_GZIP_COMPRESSION = "GZIP_COMPRESSION";
        public static final String PARAM_GZIP_COMPRESSION_TYPES = "GZIP_COMPRESSION_TYPES";
        public static final String PARAM_LOCAL_FORWARD_PORT = "LOCAL_FORWARD_PORT";
        public static final String PARAM_PROXY_ROOTURL = "PROXY_ROOTURL";
        public static final String PARAM_WEBAPP_CONTEXT_PATH = "WEBAPP_CONTEXT_PATH";
        public static final String PARAM_RESPONSE_REQUIRES_SITE = "RESPONSE_REQUIRES_SITE";
        public static final String PARAM_SITE_INITIALIZING_REDIRECT_URL = "SITE_INITIALIZING_REDIRECT_URL";
        public static final String PARAM_SITE_INITIALIZING_PASSTHROUGH_SUFFIXES = "SITE_INITIALIZING_PASSTHROUGH_SUFFIXES";
        public static final String PARAM_REQUEST_ENCODING = "REQUEST_ENCODING";
        public static final String PARAM_RESPONSE_ENCODING = "RESPONSE_ENCODING";
        public static final String PARAM_SESSION_STATE_STORE_CLONING = "SESSION_STATE_STORE_CLONING";
        private static final String DEFAULT_DEFAULT_CONTENT_TYPE = "text/html";
        private static final boolean DEFAULT_ELEMENT_AUTO_RELOAD = true;
        private static final boolean DEFAULT_ELEMENT_DEBUG_TRACE = false;
        private static final boolean DEFAULT_ELEMENT_DEBUG_MEMORY = false;
        private static final boolean DEFAULT_LOG_ENGINE_EXCEPTIONS = true;
        private static final boolean DEFAULT_PRETTY_ENGINE_EXCEPTIONS = true;
        private static final long DEFAULT_FILEUPLOAD_SIZE_LIMIT = 1024 * 1024 * 2;    // 2MB
        private static final boolean DEFAULT_FILEUPLOAD_SIZE_CHECK = true;
        private static final boolean DEFAULT_FILEUPLOAD_SIZE_EXCEPTION = false;
        private static final boolean DEFAULT_SITE_AUTO_RELOAD = true;
        private static final boolean DEFAULT_GZIP_COMPRESSION = false;
        private static final Collection<String> DEFAULT_GZIP_COMPRESSION_TYPES = new ArrayList<String>()
        {{
                add("text/html");
                add("text/xml");
                add("text/plain");
                add("text/css");
                add("text/javascript");
                add("application/xml");
                add("application/xhtml+xml");
            }};
        private static final int DEFAULT_LOCAL_FORWARD_PORT = -1;
        private static final String DEFAULT_PROXY_ROOTURL = null;
        private static final String DEFAULT_WEBAPP_CONTEXT_PATH = null;
        private static final boolean DEFAULT_RESPONSE_REQUIRES_SITE = true;
        private static final String DEFAULT_SITE_INITIALIZING_REDIRECT_URL = null;
        private static final Collection<String> DEFAULT_SITE_INITIALIZING_PASSTHROUGH_SUFFIXES = new ArrayList<String>()
        {{
                add(".gif");
                add(".png");
                add(".jpg");
                add(".jpeg");
                add(".bmp");
                add(".ico");
                add(".css");
                add(".js");
                add(".swf");
                add(".html");
                add(".htm");
                add(".htc");
                add(".class");
                add(".jar");
                add(".zip");
                add(".arj");
                add(".gz");
                add(".z");
                add(".wav");
                add(".mp3");
                add(".wma");
                add(".mpg");
                add(".avi");
                add(".ogg");
                add(".txt");
            }};
        private static final String DEFAULT_REQUEST_ENCODING = StringUtils.ENCODING_UTF_8;
        private static final String DEFAULT_RESPONSE_ENCODING = StringUtils.ENCODING_UTF_8;
        private static final boolean DEFAULT_SESSION_STATE_STORE_CLONING = true;
        private static boolean sLogEngineExceptions = DEFAULT_LOG_ENGINE_EXCEPTIONS;

        public static String getDefaultContentType()
        {
            if (Config.hasRepInstance())
            {
                return Config.getRepInstance().getString(PARAM_DEFAULT_CONTENT_TYPE, DEFAULT_DEFAULT_CONTENT_TYPE);
            }

            return DEFAULT_DEFAULT_CONTENT_TYPE;
        }

        public static boolean getElementAutoReload()
        {
            Object value = System.getProperties().get(PARAM_ELEMENT_AUTO_RELOAD);
            return Convert.toBoolean(value, DEFAULT_ELEMENT_AUTO_RELOAD);
        }

        public static String getElementGenerationPath()
        {
            String generation_path = null;

            Object value = System.getProperties().get(PARAM_ELEMENT_GENERATION_PATH);
            if (value != null)
            {
                generation_path = value.toString();
            }

            if (null == generation_path)
            {
                return RifeConfig.Global.getTempPath() + File.separator + "rife_elements";
            }

            generation_path += File.separator;

            return generation_path;
        }

        public static boolean getElementDebugTrace()
        {
            if (Config.hasRepInstance())
            {
                return Config.getRepInstance().getBool(PARAM_ELEMENT_DEBUG_TRACE, DEFAULT_ELEMENT_DEBUG_TRACE);
            }

            return DEFAULT_ELEMENT_DEBUG_TRACE;
        }

        public static boolean getElementDebugMemory()
        {
            if (Config.hasRepInstance())
            {
                return Config.getRepInstance().getBool(PARAM_ELEMENT_DEBUG_MEMORY, DEFAULT_ELEMENT_DEBUG_MEMORY);
            }

            return DEFAULT_ELEMENT_DEBUG_MEMORY;
        }

        public static boolean getPrettyEngineExceptions()
        {
            if (Config.hasRepInstance())
            {
                return Config.getRepInstance().getBool(PARAM_PRETTY_ENGINE_EXCEPTIONS, DEFAULT_PRETTY_ENGINE_EXCEPTIONS);
            }

            return DEFAULT_PRETTY_ENGINE_EXCEPTIONS;
        }

        public static boolean getLogEngineExceptions()
        {
            if (Config.hasRepInstance() &&
                Config.getRepInstance().hasParameter(PARAM_LOG_ENGINE_EXCEPTIONS))
            {
                return Config.getRepInstance().getBool(PARAM_LOG_ENGINE_EXCEPTIONS, DEFAULT_LOG_ENGINE_EXCEPTIONS);
            }

            return sLogEngineExceptions;
        }

        public static synchronized void setLogEngineExceptions(boolean generate)
        {
            if (Config.hasRepInstance())
            {
                Config.getRepInstance().setParameter(PARAM_LOG_ENGINE_EXCEPTIONS, generate);
            }
            else
            {
                sLogEngineExceptions = generate;
            }
        }

        public static String getFileUploadPath()
        {
            String fileupload_path = null;

            if (Config.hasRepInstance())
            {
                fileupload_path = Config.getRepInstance().getString(PARAM_FILEUPLOAD_PATH);
            }
            if (null == fileupload_path)
            {
                return RifeConfig.Global.getTempPath() + File.separator + "rife_uploads";
            }

            fileupload_path += File.separator;

            return fileupload_path;
        }

        public static long getFileuploadSizeLimit()
        {
            if (Config.hasRepInstance())
            {
                return Config.getRepInstance().getLong(PARAM_FILEUPLOAD_SIZE_LIMIT, DEFAULT_FILEUPLOAD_SIZE_LIMIT);
            }

            return DEFAULT_FILEUPLOAD_SIZE_LIMIT;
        }

        public static boolean getFileUploadSizeCheck()
        {
            if (Config.hasRepInstance())
            {
                return Config.getRepInstance().getBool(PARAM_FILEUPLOAD_SIZE_CHECK, DEFAULT_FILEUPLOAD_SIZE_CHECK);
            }

            return DEFAULT_FILEUPLOAD_SIZE_CHECK;
        }

        public static boolean getFileUploadSizeException()
        {
            if (Config.hasRepInstance())
            {
                return Config.getRepInstance().getBool(PARAM_FILEUPLOAD_SIZE_EXCEPTION, DEFAULT_FILEUPLOAD_SIZE_EXCEPTION);
            }

            return DEFAULT_FILEUPLOAD_SIZE_EXCEPTION;
        }

        public static boolean getSiteAutoReload()
        {
            if (Config.hasRepInstance())
            {
                return Config.getRepInstance().getBool(PARAM_SITE_AUTO_RELOAD, DEFAULT_SITE_AUTO_RELOAD);
            }

            return DEFAULT_SITE_AUTO_RELOAD;
        }
// TODO
//        public static long getContinuationDuration()
//        {
//            if (Config.hasRepInstance())
//            {
//                return Config.getRepInstance().getLong(PARAM_CONTINUATION_DURATION, ContinuationConfigRuntimeDefaults.DEFAULT_CONTINUATION_DURATION);
//            }
//
//            return ContinuationConfigRuntimeDefaults.DEFAULT_CONTINUATION_DURATION;
//        }
//
//        public static int getContinuationPurgeFrequency()
//        {
//            if (Config.hasRepInstance())
//            {
//                return Config.getRepInstance().getInt(PARAM_CONTINUATION_PURGE_FREQUENCY, ContinuationConfigRuntimeDefaults.DEFAULT_CONTINUATION_PURGE_FREQUENCY);
//            }
//
//            return ContinuationConfigRuntimeDefaults.DEFAULT_CONTINUATION_PURGE_FREQUENCY;
//        }
//
//        public static int getContinuationPurgeScale()
//        {
//            if (Config.hasRepInstance())
//            {
//                return Config.getRepInstance().getInt(PARAM_CONTINUATION_PURGE_SCALE, ContinuationConfigRuntimeDefaults.DEFAULT_CONTINUATION_PURGE_SCALE);
//            }
//
//            return ContinuationConfigRuntimeDefaults.DEFAULT_CONTINUATION_PURGE_SCALE;
//        }

        public static boolean getGzipCompression()
        {
            if (Config.hasRepInstance())
            {
                return Config.getRepInstance().getBool(PARAM_GZIP_COMPRESSION, DEFAULT_GZIP_COMPRESSION);
            }

            return DEFAULT_GZIP_COMPRESSION;
        }

        public static Collection<String> getGzipCompressionTypes()
        {
            if (Config.hasRepInstance())
            {
                Collection<String> types = Config.getRepInstance().getStringItems(PARAM_GZIP_COMPRESSION_TYPES);
                if (null == types)
                {
                    return DEFAULT_GZIP_COMPRESSION_TYPES;
                }

                return types;
            }

            return DEFAULT_GZIP_COMPRESSION_TYPES;
        }

        public static int getLocalForwardPort()
        {
            if (Config.hasRepInstance())
            {
                return Config.getRepInstance().getInt(PARAM_LOCAL_FORWARD_PORT, DEFAULT_LOCAL_FORWARD_PORT);
            }

            return DEFAULT_LOCAL_FORWARD_PORT;
        }

        public static String getProxyRootUrl()
        {
            if (Config.hasRepInstance())
            {
                return Config.getRepInstance().getString(PARAM_PROXY_ROOTURL, DEFAULT_PROXY_ROOTURL);
            }

            return DEFAULT_PROXY_ROOTURL;
        }

        public static String getWebappContextPath()
        {
            if (Config.hasRepInstance())
            {
                return Config.getRepInstance().getString(PARAM_WEBAPP_CONTEXT_PATH, DEFAULT_WEBAPP_CONTEXT_PATH);
            }

            return DEFAULT_WEBAPP_CONTEXT_PATH;
        }

        public static boolean getResponseRequiresSite()
        {
            if (Config.hasRepInstance())
            {
                return Config.getRepInstance().getBool(PARAM_RESPONSE_REQUIRES_SITE, DEFAULT_RESPONSE_REQUIRES_SITE);
            }

            return DEFAULT_RESPONSE_REQUIRES_SITE;
        }

        public static String getSiteInitializingRedirectUrl()
        {
            if (Config.hasRepInstance())
            {
                return Config.getRepInstance().getString(PARAM_SITE_INITIALIZING_REDIRECT_URL, DEFAULT_SITE_INITIALIZING_REDIRECT_URL);
            }

            return DEFAULT_SITE_INITIALIZING_REDIRECT_URL;
        }

        public static Collection<String> getSiteInitializingPassthroughSuffixes()
        {
            if (Config.hasRepInstance())
            {
                Collection<String> types = Config.getRepInstance().getStringItems(PARAM_SITE_INITIALIZING_PASSTHROUGH_SUFFIXES);
                if (null == types)
                {
                    return DEFAULT_SITE_INITIALIZING_PASSTHROUGH_SUFFIXES;
                }

                return types;
            }

            return DEFAULT_SITE_INITIALIZING_PASSTHROUGH_SUFFIXES;
        }

        public static String getRequestEncoding()
        {
            if (Config.hasRepInstance())
            {
                return Config.getRepInstance().getString(PARAM_REQUEST_ENCODING, DEFAULT_REQUEST_ENCODING);
            }

            return DEFAULT_REQUEST_ENCODING;
        }

        public static String getResponseEncoding()
        {
            if (Config.hasRepInstance())
            {
                return Config.getRepInstance().getString(PARAM_RESPONSE_ENCODING, DEFAULT_RESPONSE_ENCODING);
            }

            return DEFAULT_RESPONSE_ENCODING;
        }

        public static boolean getSessionStateStoreCloning()
        {
            if (Config.hasRepInstance())
            {
                return Config.getRepInstance().getBool(PARAM_SESSION_STATE_STORE_CLONING, DEFAULT_SESSION_STATE_STORE_CLONING);
            }

            return DEFAULT_SESSION_STATE_STORE_CLONING;
        }
    }

    public static abstract class Mime
    {
        public static final String PARAM_MIME_MAPPING = "MIME_MAPPING";
        private static Map<String, String> DEFAULT_MIME_MAPPING = new HashMap<String, String>()
        {{
                put("ez", "application/andrew-inset");
                put("jnlp", "application/jnlp");
                put("hqx", "application/mac-binhex40");
                put("cpt", "application/mac-compactpro");
                put("mathml", "application/mathml+xml");
                put("bin", "application/octet-stream");
                put("dms", "application/octet-stream");
                put("lha", "application/octet-stream");
                put("lzh", "application/octet-stream");
                put("exe", "application/octet-stream");
                put("class", "application/octet-stream");
                put("so", "application/octet-stream");
                put("dll", "application/octet-stream");
                put("dmg", "application/octet-stream");
                put("oda", "application/oda");
                put("ogg", "application/ogg");
                put("pdf", "application/pdf");
                put("ai", "application/postscript");
                put("eps", "application/postscript");
                put("ps", "application/postscript");
                put("rdf", "application/rdf+xml");
                put("smi", "application/smil");
                put("smil", "application/smil");
                put("gram", "application/srgs");
                put("grxml", "application/srgs+xml");
                put("mif", "application/vnd.mif");
                put("xls", "application/vnd.ms-excel");
                put("ppt", "application/vnd.ms-powerpoint");
                put("rm", "application/vnd.rn-realmedia");
                put("bcpio", "application/x-bcpio");
                put("vcd", "application/x-cdlink");
                put("pgn", "application/x-chess-pgn");
                put("cpio", "application/x-cpio");
                put("csh", "application/x-csh");
                put("dcr", "application/x-director");
                put("dir", "application/x-director");
                put("dxr", "application/x-director");
                put("dvi", "application/x-dvi");
                put("spl", "application/x-futuresplash");
                put("gtar", "application/x-gtar");
                put("hdf", "application/x-hdf");
                put("js", "application/x-javascript");
                put("skp", "application/x-koan");
                put("skd", "application/x-koan");
                put("skt", "application/x-koan");
                put("skm", "application/x-koan");
                put("latex", "application/x-latex");
                put("nc", "application/x-netcdf");
                put("cdf", "application/x-netcdf");
                put("ogg", "application/x-ogg");
                put("sh", "application/x-sh");
                put("shar", "application/x-shar");
                put("swf", "application/x-shockwave-flash");
                put("sit", "application/x-stuffit");
                put("sv4cpio", "application/x-sv4cpio");
                put("sv4crc", "application/x-sv4crc");
                put("tar", "application/x-tar");
                put("tcl", "application/x-tcl");
                put("tex", "application/x-tex");
                put("texinfo", "application/x-texinfo");
                put("texi", "application/x-texinfo");
                put("t", "application/x-troff");
                put("tr", "application/x-troff");
                put("roff", "application/x-troff");
                put("man", "application/x-troff-man");
                put("me", "application/x-troff-me");
                put("ms", "application/x-troff-ms");
                put("ustar", "application/x-ustar");
                put("src", "application/x-wais-source");
                put("xhtml", "application/xhtml+xml");
                put("xht", "application/xhtml+xml");
                put("xslt", "application/xslt+xml");
                put("xml", "application/xml");
                put("xsl", "application/xml");
                put("dtd", "application/xml-dtd");
                put("zip", "application/zip");
                put("au", "audio/basic");
                put("snd", "audio/basic");
                put("mid", "audio/midi");
                put("midi", "audio/midi");
                put("kar", "audio/midi");
                put("mpga", "audio/mpeg");
                put("mp2", "audio/mpeg");
                put("mp3", "audio/mpeg");
                put("aif", "audio/x-aiff");
                put("aiff", "audio/x-aiff");
                put("aifc", "audio/x-aiff");
                put("m3u", "audio/x-mpegurl");
                put("ram", "audio/x-pn-realaudio");
                put("ra", "audio/x-pn-realaudio");
                put("wav", "audio/x-wav");
                put("pdb", "chemical/x-pdb");
                put("xyz", "chemical/x-xyz");
                put("bmp", "image/bmp");
                put("cgm", "image/cgm");
                put("gif", "image/gif");
                put("ief", "image/ief");
                put("jpeg", "image/jpeg");
                put("jpg", "image/jpeg");
                put("jpe", "image/jpeg");
                put("png", "image/png");
                put("svg", "image/svg+xml");
                put("tiff", "image/tiff");
                put("tif", "image/tiff");
                put("djvu", "image/vnd.djvu");
                put("djv", "image/vnd.djvu");
                put("wbmp", "image/vnd.wap.wbmp");
                put("ras", "image/x-cmu-raster");
                put("ico", "image/x-icon");
                put("pnm", "image/x-portable-anymap");
                put("pbm", "image/x-portable-bitmap");
                put("pgm", "image/x-portable-graymap");
                put("ppm", "image/x-portable-pixmap");
                put("rgb", "image/x-rgb");
                put("xbm", "image/x-xbitmap");
                put("xpm", "image/x-xpixmap");
                put("xwd", "image/x-xwindowdump");
                put("igs", "model/iges");
                put("iges", "model/iges");
                put("msh", "model/mesh");
                put("mesh", "model/mesh");
                put("silo", "model/mesh");
                put("wrl", "model/vrml");
                put("vrml", "model/vrml");
                put("ics", "text/calendar");
                put("ifb", "text/calendar");
                put("css", "text/css");
                put("html", "text/html");
                put("htm", "text/html");
                put("asc", "text/plain");
                put("txt", "text/plain");
                put("rtx", "text/richtext");
                put("rtf", "text/rtf");
                put("sgml", "text/sgml");
                put("sgm", "text/sgml");
                put("tsv", "text/tab-separated-values");
                put("wml", "text/vnd.wap.wml");
                put("wmls", "text/vnd.wap.wmlscript");
                put("etx", "text/x-setext");
                put("htc", "text/x-component");
                put("mpeg", "video/mpeg");
                put("mpg", "video/mpeg");
                put("mpe", "video/mpeg");
                put("qt", "video/quicktime");
                put("mov", "video/quicktime");
                put("mxu", "video/vnd.mpegurl");
                put("m4u", "video/vnd.mpegurl");
                put("avi", "video/x-msvideo");
                put("movie", "video/x-sgi-movie");
                put("ice", "x-conference/x-cooltalk");
            }};

        public static String getMimeType(String extension)
        {
            return DEFAULT_MIME_MAPPING.get(extension);
        }
    }

    public static abstract class Resources
    {
        public static final String PARAM_TABLE_RESOURCES = "TABLE_RESOURCES";
        private static final String DEFAULT_TABLE_TASK = "Resources";

        public static String getTableResources()
        {
            if (Config.hasRepInstance())
            {
                return Config.getRepInstance().getString(PARAM_TABLE_RESOURCES, DEFAULT_TABLE_TASK);
            }

            return DEFAULT_TABLE_TASK;
        }
    }

    public static abstract class Scheduler
    {
        public static final String PARAM_TABLE_TASK = "TABLE_TASK";
        public static final String PARAM_SEQUENCE_TASK = "SEQUENCE_TASK";
        public static final String PARAM_TABLE_TASKOPTION = "TABLE_TASKOPTION";
        public static final String PARAM_TASKOPTION_NAME_MAXIMUM_LENGTH = "TASKOPTION_NAME_MAXIMUM_LENGTH";
        public static final String PARAM_TASKOPTION_VALUE_MAXIMUM_LENGTH = "TASKOPTION_VALUE_MAXIMUM_LENGTH";
        public static final String PARAM_TASK_TYPE_MAXIMUM_LENGTH = "TASK_TYPE_MAXIMUM_LENGTH";
        public static final String PARAM_TASK_FREQUENCY_MAXIMUM_LENGTH = "TASK_FREQUENCY_MAXIMUM_LENGTH";
        private static final String DEFAULT_TABLE_TASK = "SchedTask";
        private static final String DEFAULT_SEQUENCE_TASK = "SEQ_SCHEDTASK";
        private static final String DEFAULT_TABLE_TASKOPTION = "SchedTaskoption";
        private static final int DEFAULT_TASKOPTION_NAME_MAXIMUM_LENGTH = 255;
        private static final int DEFAULT_TASKOPTION_VALUE_MAXIMUM_LENGTH = 255;
        private static final int DEFAULT_TASK_TYPE_MAXIMUM_LENGTH = 255;
        private static final int DEFAULT_TASK_FREQUENCY_MAXIMUM_LENGTH = 255;

        public static int getTaskTypeMaximumLength()
        {
            if (Config.hasRepInstance())
            {
                return Config.getRepInstance().getInt(PARAM_TASK_TYPE_MAXIMUM_LENGTH, DEFAULT_TASK_TYPE_MAXIMUM_LENGTH);
            }

            return DEFAULT_TASK_TYPE_MAXIMUM_LENGTH;
        }

        public static int getTaskFrequencyMaximumLength()
        {
            if (Config.hasRepInstance())
            {
                return Config.getRepInstance().getInt(PARAM_TASK_FREQUENCY_MAXIMUM_LENGTH, DEFAULT_TASK_FREQUENCY_MAXIMUM_LENGTH);
            }

            return DEFAULT_TASK_FREQUENCY_MAXIMUM_LENGTH;
        }

        public static int getTaskoptionValueMaximumLength()
        {
            if (Config.hasRepInstance())
            {
                return Config.getRepInstance().getInt(PARAM_TASKOPTION_VALUE_MAXIMUM_LENGTH, DEFAULT_TASKOPTION_VALUE_MAXIMUM_LENGTH);
            }

            return DEFAULT_TASKOPTION_VALUE_MAXIMUM_LENGTH;
        }

        public static int getTaskoptionNameMaximumLength()
        {
            if (Config.hasRepInstance())
            {
                return Config.getRepInstance().getInt(PARAM_TASKOPTION_NAME_MAXIMUM_LENGTH, DEFAULT_TASKOPTION_NAME_MAXIMUM_LENGTH);
            }

            return DEFAULT_TASKOPTION_NAME_MAXIMUM_LENGTH;
        }

        public static String getTableTask()
        {
            if (Config.hasRepInstance())
            {
                return Config.getRepInstance().getString(PARAM_TABLE_TASK, DEFAULT_TABLE_TASK);
            }

            return DEFAULT_TABLE_TASK;
        }

        public static String getSequenceTask()
        {
            if (Config.hasRepInstance())
            {
                return Config.getRepInstance().getString(PARAM_SEQUENCE_TASK, DEFAULT_SEQUENCE_TASK);
            }

            return DEFAULT_SEQUENCE_TASK;
        }

        public static String getTableTaskoption()
        {
            if (Config.hasRepInstance())
            {
                return Config.getRepInstance().getString(PARAM_TABLE_TASKOPTION, DEFAULT_TABLE_TASKOPTION);
            }

            return DEFAULT_TABLE_TASKOPTION;
        }
    }

    public static abstract class Swing
    {
        public static final String PARAM_ICON_CONFIRM_PATH = "ICON_CONFIRM_PATH";
        public static final String PARAM_ICON_ERROR_PATH = "ICON_ERROR_PATH";
        public static final String PARAM_ICON_INFO_PATH = "ICON_INFO_PATH";
        private static final String DEFAULT_ICON_CONFIRM_PATH = "icons/confirm.gif";
        private static final String DEFAULT_ICON_ERROR_PATH = "icons/error.gif";
        private static final String DEFAULT_ICON_INFO_PATH = "icons/info.gif";

        public static String getIconConfirmPath()
        {
            if (Config.hasRepInstance())
            {
                return Config.getRepInstance().getString(PARAM_ICON_CONFIRM_PATH, DEFAULT_ICON_CONFIRM_PATH);
            }

            return DEFAULT_ICON_CONFIRM_PATH;
        }

        public static String getIconErrorPath()
        {
            if (Config.hasRepInstance())
            {
                return Config.getRepInstance().getString(PARAM_ICON_ERROR_PATH, DEFAULT_ICON_ERROR_PATH);
            }

            return DEFAULT_ICON_ERROR_PATH;
        }

        public static String getIconInfoPath()
        {
            if (Config.hasRepInstance())
            {
                return Config.getRepInstance().getString(PARAM_ICON_INFO_PATH, DEFAULT_ICON_INFO_PATH);
            }

            return DEFAULT_ICON_INFO_PATH;
        }
    }

//	public static abstract class Template
//	{
//		public static final String PARAM_TEMPLATE_AUTO_RELOAD = "TEMPLATE_AUTO_RELOAD";
//		public static final String PARAM_TEMPLATE_GENERATION_PATH = "TEMPLATE_GENERATION_PATH";
//		public static final String PARAM_TEMPLATE_GENERATE_CLASSES = "TEMPLATE_GENERATE_CLASSES";
//		public static final String PARAM_TEMPLATE_DEFAULT_ENCODING = "TEMPLATE_DEFAULT_ENCODING";
//
//		public static final String PREFIX_TEMPLATE_DEFAULT_RESOURCEBUNDLES = "TEMPLATE_DEFAULT_RESOURCEBUNDLES_";
//
//		private static String								sGenerationPath = null;
//		private static String								sDefaultEncoding = null;
//		private static HashMap<String, Collection<String>>	sDefaultResourcebundles = null;
//		private static boolean								sGenerateClasses = false;
//
//		private static final boolean	DEFAULT_TEMPLATE_AUTO_RELOAD = true;
//
//		public static boolean getAutoReload()
//		{
//			if (Config.hasRepInstance())
//			{
//				return Config.getRepInstance().getBool(PARAM_TEMPLATE_AUTO_RELOAD, DEFAULT_TEMPLATE_AUTO_RELOAD);
//			}
//
//			return DEFAULT_TEMPLATE_AUTO_RELOAD;
//		}
//
//		public static String getGenerationPath()
//		{
//			String generation_path = null;
//
//			if (Config.hasRepInstance())
//			{
//				generation_path = Config.getRepInstance().getString(PARAM_TEMPLATE_GENERATION_PATH, sGenerationPath);
//			}
//			if (null == generation_path)
//			{
//				generation_path = sGenerationPath;
//			}
//			if (null == generation_path)
//			{
//				return RifeConfig.Global.getTempPath()+File.separator+"rife_templates";
//			}
//
//			generation_path += File.separator;
//
//			return generation_path;
//		}
//
//		public static synchronized void setGenerationPath(String path)
//		{
//			if (null == path)		throw new IllegalArgumentException("path can't be null.");
//			if (0 == path.length())	throw new IllegalArgumentException("path can't be empty.");
//
//			if (Config.hasRepInstance())
//			{
//				Config.getRepInstance().setParameter(PARAM_TEMPLATE_GENERATION_PATH, path);
//			}
//			else
//			{
//				sGenerationPath = path;
//			}
//		}
//
//		public static boolean getGenerateClasses()
//		{
//			if (Config.hasRepInstance() &&
//				Config.getRepInstance().hasParameter(PARAM_TEMPLATE_GENERATE_CLASSES))
//			{
//				return Config.getRepInstance().getBool(PARAM_TEMPLATE_GENERATE_CLASSES, sGenerateClasses);
//			}
//
//			return sGenerateClasses;
//		}
//
//		public static synchronized void setGenerateClasses(boolean generate)
//		{
//			if (Config.hasRepInstance())
//			{
//				Config.getRepInstance().setParameter(PARAM_TEMPLATE_GENERATE_CLASSES, generate);
//			}
//			else
//			{
//				sGenerateClasses = generate;
//			}
//		}
//
//		public static String getDefaultEncoding()
//		{
//			if (Config.hasRepInstance())
//			{
//				return Config.getRepInstance().getString(PARAM_TEMPLATE_DEFAULT_ENCODING, sDefaultEncoding);
//			}
//
//			return sDefaultEncoding;
//		}
//
//		public static synchronized void setDefaultEncoding(String encoding)
//		{
//			if (null == encoding)		throw new IllegalArgumentException("encoding can't be null.");
//			if (0 == encoding.length())	throw new IllegalArgumentException("encoding can't be empty.");
//
//			if (Config.hasRepInstance())
//			{
//				Config.getRepInstance().setParameter(PARAM_TEMPLATE_DEFAULT_ENCODING, encoding);
//			}
//			else
//			{
//				sDefaultEncoding = encoding;
//			}
//		}
//
//		public static Collection<String> getDefaultResourcebundles(TemplateFactory factory)
//		{
//			Collection<String> result = null;
//
//			if (Config.hasRepInstance())
//			{
//				result = Config.getRepInstance().getStringItems(PREFIX_TEMPLATE_DEFAULT_RESOURCEBUNDLES+factory.getIdentifierUppercase());
//			}
//
//			if (null == result &&
//				sDefaultResourcebundles != null)
//			{
//				result = sDefaultResourcebundles.get(factory.getIdentifierUppercase());
//			}
//
//			return result;
//		}
//
//		public static String getDefaultResourcebundle(TemplateFactory factory)
//		{
//			Collection<String> result = getDefaultResourcebundles(factory);
//			if (null == result || 0 == result.size())
//			{
//				return null;
//			}
//			return result.iterator().next();
//		}
//
//		public static synchronized void setDefaultResourcebundles(TemplateFactory factory, Collection<String> bundles)
//		{
//			if (Config.hasRepInstance())
//			{
//				String param = PREFIX_TEMPLATE_DEFAULT_RESOURCEBUNDLES+factory.getIdentifierUppercase();
//
//				Config.getRepInstance().removeList(param);
//				if (bundles != null)
//				{
//					for (String bundle : bundles)
//					{
//						Config.getRepInstance().addListItem(param, bundle);
//					}
//				}
//			}
//			else
//			{
//				if (null == sDefaultResourcebundles)
//				{
//					sDefaultResourcebundles = new HashMap<String, Collection<String>>();
//				}
//
//				sDefaultResourcebundles.put(factory.getIdentifierUppercase(), bundles);
//			}
//		}
//	}

    public static abstract class Tools
    {
        public static final String PARAM_L10N_RESOURCEBUNDLE_AUTO_RELOAD = "L10N_RESOURCEBUNDLE_AUTO_RELOAD";
        public static final String PARAM_L10N_DEFAULT_RESOURCEBUNDLE = "L10N_DEFAULT_RESOURCEBUNDLE";
        public static final String PARAM_L10N_DEFAULT_LANGUAGE = "L10N_DEFAULT_LANGUAGE";
        public static final String PARAM_L10N_DEFAULT_COUNTRY = "L10N_DEFAULT_COUNTRY";
        public static final String PARAM_L10N_DEFAULT_TIMEZONE = "L10N_DEFAULT_TIMEZONE";
        public static final String PARAM_L10N_DEFAULT_SHORT_DATEFORMAT = "L10N_DEFAULT_SHORT_DATEFORMAT";
        public static final String PARAM_L10N_DEFAULT_LONG_DATEFORMAT = "L10N_DEFAULT_LONG_DATEFORMAT";
        public static final String PARAM_L10N_DEFAULT_INPUT_DATEFORMAT = "L10N_DEFAULT_INPUT_DATEFORMAT";
        public static final String PARAM_MAX_VISUAL_URL_LENGTH = "MAX_VISUAL_URL_LENGTH";
        private static final String DEFAULT_LANGUAGE = "en";
        private static final int DEFAULT_MAX_VISUAL_URL_LENGTH = 70;
        private static final boolean DEFAULT_RESOURCEBUNDLE_AUTO_RELOAD = true;
        private static String sDefaultResourcebundle = null;
        private static String sDefaultLanguage = DEFAULT_LANGUAGE;
        private static String sDefaultCountry = null;
        private static TimeZone sDefaultTimeZone = null;

        public static boolean getResourcebundleAutoReload()
        {
            if (Config.hasRepInstance())
            {
                return Config.getRepInstance().getBool(PARAM_L10N_RESOURCEBUNDLE_AUTO_RELOAD, DEFAULT_RESOURCEBUNDLE_AUTO_RELOAD);
            }

            return DEFAULT_RESOURCEBUNDLE_AUTO_RELOAD;
        }

        public static String getDefaultResourceBundle()
        {
            if (Config.hasRepInstance())
            {
                return Config.getRepInstance().getString(PARAM_L10N_DEFAULT_RESOURCEBUNDLE, sDefaultResourcebundle);
            }

            return sDefaultResourcebundle;
        }

        public static synchronized void setDefaultResourceBundle(String name)
        {
            if (name != null &&
                0 == name.length())
            {
                throw new IllegalArgumentException("name can't be empty.");
            }

            if (Config.hasRepInstance())
            {
                if (null == name)
                {
                    Config.getRepInstance().removeParameter(PARAM_L10N_DEFAULT_RESOURCEBUNDLE);
                }
                else
                {
                    Config.getRepInstance().setParameter(PARAM_L10N_DEFAULT_RESOURCEBUNDLE, name);
                }
            }
            else
            {
                sDefaultResourcebundle = name;
            }
        }

        public static String getDefaultLanguage()
        {
            if (Config.hasRepInstance())
            {
                return Config.getRepInstance().getString(PARAM_L10N_DEFAULT_LANGUAGE, sDefaultLanguage);
            }

            return sDefaultLanguage;
        }

        public static synchronized void setDefaultLanguage(String abbreviation)
        {
            if (abbreviation != null &&
                0 == abbreviation.length())
            {
                throw new IllegalArgumentException("abbreviation can't be empty.");
            }

            if (Config.hasRepInstance())
            {
                if (null == abbreviation)
                {
                    Config.getRepInstance().removeParameter(PARAM_L10N_DEFAULT_LANGUAGE);
                }
                else
                {
                    Config.getRepInstance().setParameter(PARAM_L10N_DEFAULT_LANGUAGE, abbreviation);
                }
            }
            else
            {
                if (null == abbreviation)
                {
                    sDefaultLanguage = DEFAULT_LANGUAGE;
                }
                else
                {
                    sDefaultLanguage = abbreviation;
                }
            }
        }

        public static String getDefaultCountry()
        {
            if (Config.hasRepInstance())
            {
                return Config.getRepInstance().getString(PARAM_L10N_DEFAULT_COUNTRY, sDefaultCountry);
            }

            return sDefaultCountry;
        }

        public static synchronized void setDefaultCountry(String countryCode)
        {
            if (null == countryCode)
            {
                throw new IllegalArgumentException("countryCode can't be null.");
            }
            if (0 == countryCode.length())
            {
                throw new IllegalArgumentException("countryCode can't be empty.");
            }

            if (Config.hasRepInstance())
            {
                Config.getRepInstance().setParameter(PARAM_L10N_DEFAULT_COUNTRY, countryCode);
            }
            else
            {
                sDefaultCountry = countryCode;
            }
        }

        public static TimeZone getDefaultTimeZone()
        {
            TimeZone result = sDefaultTimeZone;

            if (null == result &&
                Config.hasRepInstance())
            {
                String timezoneid = Config.getRepInstance().getString(PARAM_L10N_DEFAULT_TIMEZONE);
                if (timezoneid != null)
                {
                    result = TimeZone.getTimeZone(timezoneid);
                }
            }

            if (null == result)
            {
                result = TimeZone.getDefault();
            }

            return result;
        }

        public static synchronized void setDefaultTimeZone(TimeZone timeZone)
        {
            if (Config.hasRepInstance())
            {
                Config.getRepInstance().setParameter(PARAM_L10N_DEFAULT_TIMEZONE, timeZone.getID());
            }
            else
            {
                sDefaultTimeZone = timeZone;
            }
        }

        public static DateFormat getDefaultShortDateFormat()
        {
            if (Config.hasRepInstance())
            {
                Config config = Config.getRepInstance();
                if (config.hasParameter(PARAM_L10N_DEFAULT_SHORT_DATEFORMAT))
                {
                    SimpleDateFormat sf;
                    try
                    {
                        sf = new SimpleDateFormat(config.getString(PARAM_L10N_DEFAULT_SHORT_DATEFORMAT), Localization.getLocale());
                        sf.setTimeZone(RifeConfig.Tools.getDefaultTimeZone());
                    }
                    catch (IllegalArgumentException e)
                    {
                        throw new DateFormatInitializationException(e.getMessage());
                    }

                    return sf;
                }
            }

            if (0 != getDefaultLanguage().compareToIgnoreCase(DEFAULT_LANGUAGE))
            {
                return DateFormat.getDateInstance(DateFormat.SHORT, Localization.getLocale());
            }

            return DateFormat.getDateInstance(DateFormat.SHORT, Locale.ENGLISH);
        }

        public static DateFormat getDefaultLongDateFormat()
        {
            if (Config.hasRepInstance())
            {
                Config config = Config.getRepInstance();
                if (config.hasParameter(PARAM_L10N_DEFAULT_LONG_DATEFORMAT))
                {
                    SimpleDateFormat sf;
                    try
                    {
                        sf = new SimpleDateFormat(config.getString(PARAM_L10N_DEFAULT_LONG_DATEFORMAT), Localization.getLocale());
                        sf.setTimeZone(RifeConfig.Tools.getDefaultTimeZone());
                    }
                    catch (IllegalArgumentException e)
                    {
                        throw new DateFormatInitializationException(e.getMessage());
                    }

                    return sf;
                }
            }

            if (0 != getDefaultLanguage().compareToIgnoreCase(DEFAULT_LANGUAGE))
            {
                return DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT, Localization.getLocale());
            }

            return DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT, Locale.ENGLISH);
        }

        public static DateFormat getDefaultInputDateFormat()
        {
            if (Config.hasRepInstance())
            {
                Config config = Config.getRepInstance();
                if (config.hasParameter(PARAM_L10N_DEFAULT_INPUT_DATEFORMAT))
                {
                    SimpleDateFormat sf;
                    try
                    {
                        sf = new SimpleDateFormat(config.getString(PARAM_L10N_DEFAULT_INPUT_DATEFORMAT), Localization.getLocale());
                        sf.setTimeZone(RifeConfig.Tools.getDefaultTimeZone());
                    }
                    catch (IllegalArgumentException e)
                    {
                        throw new DateFormatInitializationException(e.getMessage());
                    }

                    return sf;
                }
            }

            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            sf.setTimeZone(RifeConfig.Tools.getDefaultTimeZone());
            return sf;
        }

        public static int getMaxVisualUrlLength()
        {
            if (Config.hasRepInstance())
            {
                return Config.getRepInstance().getInt(PARAM_MAX_VISUAL_URL_LENGTH, DEFAULT_MAX_VISUAL_URL_LENGTH);
            }

            return DEFAULT_MAX_VISUAL_URL_LENGTH;
        }
    }

    public static abstract class Xml
    {
        public static final String PARAM_XML_VALIDATION = "XML_VALIDATION";
        private static final boolean DEFAULT_XML_VALIDATION = true;

        public static boolean getXmlValidation()
        {
            if (Config.hasRepInstance())
            {
                return Config.getRepInstance().getBool(PARAM_XML_VALIDATION, DEFAULT_XML_VALIDATION);
            }

            return DEFAULT_XML_VALIDATION;
        }
    }
}
