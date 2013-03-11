/*
 * Copyright 2001-2008 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: Datasource.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database;

import com.uwyn.rife.database.exceptions.*;

import com.uwyn.rife.database.capabilities.CapabilitiesCompensator;
import com.uwyn.rife.database.types.SqlConversion;
import com.uwyn.rife.tools.ExceptionUtils;
import com.uwyn.rife.tools.StringUtils;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.logging.Logger;
import javax.sql.DataSource;

/**
 * Contains all the information required to connect to a database and
 * centralizes the creation of connections to a database. These connections can
 * optionally be pooled.
 * <p>
 * The initial connection will only be made and the pool will only be
 * initialized when a connection is obtained for the first time. The
 * instantiation only stores the connection parameters.
 * <p>
 * A <code>Datasource</code> also defines the type of database that is used for
 * all database-independent logic such as sql to java and java to sql type
 * mappings, query builders, database-based authentication, database-based
 * scheduling, ... The key that identifies a supported type is the class name of
 * the jdbc driver.
 * <p>
 * A <code>Datasource</code> instance can be created through it's constructor,
 * but it's recommended to work with a <code>Datasources</code> collection
 * that is created and populated through XML. This can easily be achieved by
 * using a <code>ParticipantDatasources</code> which participates in the
 * application-wide repository.
 * <p>
 * Once a connection has been obtained from a pooled datasource, modifying its
 * connection parameters is not possible anymore, a new instance has to be
 * created to set the parameters to different values.
 *
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 * @see com.uwyn.rife.database.Datasources
 * @see com.uwyn.rife.database.Xml2Datasources
 * @see com.uwyn.rife.rep.Rep
 * @see com.uwyn.rife.rep.participants.ParticipantDatasources
 * @since 1.0
 */
public class Datasource implements Cloneable
{
	static HashMap<String, String>	sDriverAliases = new HashMap<String, String>();
	static HashMap<String, String>	sDriverNames = new HashMap<String, String>();

	static
	{
		sDriverAliases.put("org.gjt.mm.mysql.Driver", "com.mysql.jdbc.Driver");
		sDriverAliases.put("in.co.daffodil.db.rmi.RmiDaffodilDBDriver", "in.co.daffodil.db.jdbc.DaffodilDBDriver");
		sDriverAliases.put("oracle.jdbc.OracleDriver", "oracle.jdbc.driver.OracleDriver");
		sDriverAliases.put("org.apache.derby.jdbc.ClientDriver", "org.apache.derby.jdbc.EmbeddedDriver");
		
		sDriverNames.put("Apache Derby Embedded JDBC Driver", "org.apache.derby.jdbc.EmbeddedDriver");
		sDriverNames.put("Apache Derby Network Client JDBC Driver", "org.apache.derby.jdbc.EmbeddedDriver");
		sDriverNames.put("DaffodilDBDriver", "in.co.daffodil.db.jdbc.DaffodilDBDriver");
		sDriverNames.put("H2 JDBC Driver", "org.h2.Driver");
		sDriverNames.put("HSQL Database Engine Driver", "org.hsqldb.jdbcDriver");
		sDriverNames.put("Jaybird JCA/JDBC driver", "org.firebirdsql.jdbc.FBDriver");
		sDriverNames.put("Mckoi JDBC Driver", "com.mckoi.JDBCDriver");
		sDriverNames.put("MySQL-AB JDBC Driver", "com.mysql.jdbc.Driver");
		sDriverNames.put("Oracle JDBC driver", "oracle.jdbc.driver.OracleDriver");
		sDriverNames.put("PostgreSQL Native Driver", "org.postgresql.Driver");
	}

	private String					mDriver = null;
	private String					mUrl = null;
	private String					mUser = null;
	private String					mPassword = null;
	private SqlConversion			mSqlConversion = null;
	private CapabilitiesCompensator	mCapabilitiesCompensator = null;
	private ConnectionPool			mConnectionPool = new ConnectionPool();
	private DataSource				mDataSource = null;

	/**
	 * Instantiates a new <code>Datasource</code> object with no connection
	 * information. The setters need to be used afterwards to provide each
	 * required parameter before the <code>Datasource</code> can be used.
	 *
	 * @see #setDriver(String)
	 * @see #setUrl(String)
	 * @see #setUser(String)
	 * @see #setPassword(String)
	 * @see #setPoolsize(int)
	 * @see #setDataSource(DataSource)
	 *
	 * @since 1.0
	 */
	public Datasource()
	{
	}

	/**
	 * Instantiates a new <code>Datasource</code> object with all the
	 * connection parameters that are required.
	 *
	 * @param driver the fully-qualified classname of the jdbc driver that will
	 * be used to connect to the database
	 * @param url the connection url which identifies the database to which the
	 * connection will be made, this is entirely driver-dependent
	 * @param user the user that will be used to connect to the database
	 * @param password the password that will be used to connect to the database
	 * @param poolsize the size of the connection pool, <code>0</code> means
	 * that the connections will not be pooled
	 *
	 * @since 1.0
	 */
	public Datasource(String driver, String url, String user, String password, int poolsize)
	{
		setDriver(driver);
		setUrl(url);
		setUser(user);
		setPassword(password);
		setPoolsize(poolsize);

		assert mDriver != null;
		assert mDriver.length() > 0;
		assert mUrl != null;
		assert mUrl.length() > 0;
	}

	/**
	 * Instantiates a new <code>Datasource</code> object from a standard
	 * <code>javax.sql.DataSource</code>.
	 * <p>
	 * The driver will be detected from the connection that is provided by this
	 * <code>DataSource</code>. If the driver couldn't be detected, an exception
	 * will be thrown upon connect.
	 *
	 * @param dataSource the standard datasource that will be used to obtain the
	 * connection
	 * @param poolsize the size of the connection pool, <code>0</code> means
	 * that the connections will not be pooled
	 *
	 * @since 1.3
	 */
    public Datasource(DataSource dataSource, int poolsize)
	{
    	setDataSource(dataSource);
		setPoolsize(poolsize);

		assert dataSource != null;
	}

	/**
	 * Instantiates a new <code>Datasource</code> object from a standard
	 * <code>javax.sql.DataSource</code>.
	 *
	 * @param dataSource the standard datasource that will be used to obtain the
	 * connection
	 * @param driver the fully-qualified classname of the jdbc driver that will
	 * be used to provide an identifier for the database abstraction functionalities,
	 * <code>null</code> will let RIFE try to figure it out by itself
	 * @param user the user that will be used to connect to the database
	 * @param password the password that will be used to connect to the database
	 * @param poolsize the size of the connection pool, <code>0</code> means
	 * that the connections will not be pooled
	 *
	 * @since 1.3
	 */
    public Datasource(DataSource dataSource, String driver, String user, String password, int poolsize)
	{
    	setDataSource(dataSource);
		mDriver = driver;
		mSqlConversion = null;
		setUser(user);
		setPassword(password);
		setPoolsize(poolsize);

		assert dataSource != null;
	}

	/**
	 * Creates a new connection by using all the parameters that have been
	 * defined in the <code>Datasource</code>.
	 *
	 * @return the newly created <code>DbConnection</code> instance
	 *
	 * @throws DatabaseException when an error occured during the creation of
	 * the connection
	 *
	 * @since 1.0
	 */
	DbConnection createConnection()
	throws DatabaseException
	{
		Connection connection = null;

		if (this.mDataSource != null)
		{
            // try to create a datasource connection
            if (null != mUser && null != mPassword)
			{
                try
				{
                    connection = this.mDataSource.getConnection(mUser, mPassword);
                }
				catch (SQLException e)
				{
                    throw new ConnectionOpenErrorException(null, mUser, mPassword, e);
                }
            }
			else
			{
                try
				{
                    connection = this.mDataSource.getConnection();
                }
				catch (SQLException e)
				{
                    throw new ConnectionOpenErrorException(null, e);
                }
            }

            if (null == mDriver)
            {
        		try
        		{
        			String driver_name = connection.getMetaData().getDriverName();
        			mDriver = sDriverNames.get(driver_name);
        			if (null == mDriver)
        			{
                        throw new UnsupportedDriverNameException(driver_name);
        			}
        		}
        		catch (SQLException e)
        		{
                    throw new DriverNameRetrievalErrorException(e);
        		}
            }
        }
		else
		{

			// obtain the jdbc driver instance
			try
			{
				Class.forName(mDriver).newInstance();
			}
			catch (InstantiationException e)
			{
				throw new DriverInstantiationErrorException(mDriver, e);
			}
			catch (ClassNotFoundException e)
			{
				throw new DriverInstantiationErrorException(mDriver, e);
			}
			catch (IllegalAccessException e)
			{
				throw new DriverInstantiationErrorException(mDriver, e);
			}

			// try to create a jdbc connection
			if (null != mUser &&
				null != mPassword)
			{
				try
				{
					connection = DriverManager.getConnection(mUrl, mUser, mPassword);
				}
				catch (SQLException e)
				{
					throw new ConnectionOpenErrorException(mUrl, mUser, mPassword, e);
				}
			}
			else
			{
				try
				{
					connection = DriverManager.getConnection(mUrl);
				}
				catch (SQLException e)
				{
					throw new ConnectionOpenErrorException(mUrl, e);
				}
			}
		}


		// returns a new DbConnection instance with contains the new jdbc
		// connection and is linked to this datasource
		return new DbConnection(connection, this);
	}

	/**
	 * Retrieves a free database connection. If no connection pool is used, a
	 * new <code>DbConnection</code> will always be created, otherwise the first
	 * available connection in the pool will be returned.
	 *
	 * @return a free <code>DbConnection</code> instance which can be used to
	 * create an execute statements
	 *
	 * @throws DatabaseException when errors occured during the creation of a
	 * new connection or during the obtainance of a connection from the pool
	 *
	 * @since 1.0
	 */
	public DbConnection getConnection()
	throws DatabaseException
	{
		return mConnectionPool.getConnection(this);
	}

	/**
	 * Retrieves the fully qualified class name of the jdbc driver that's used
	 * by this <code>Datasource</code>.
	 *
	 * @return a <code>String</code> with the name of the jdbc driver; or
	 * <p>
	 * <code>null</code> if the driver hasn't been set
	 *
	 * @see #setDriver(String)
	 * @see #getAliasedDriver()
	 *
	 * @since 1.0
	 */
	public String getDriver()
	{
		// make sure that a JNDI connection has been made first, so that the database name can be looked up
		if (mDataSource != null &&
			null == mDriver)
		{
			getConnection();
		}
		
		return mDriver;
	}

	/**
	 * Retrieves the fully qualified class name of the jdbc driver that's used
	 * by this <code>Datasource</code>. Instead of straight retrieval of the
	 * internal value, it looks for jdbc driver aliases and changes the driver
	 * classname if it's not supported by RIFE, but its alias is.
	 *
	 * @return a <code>String</code> with the name of the jdbc driver; or
	 * <p>
	 * <code>null</code> if the driver hasn't been set
	 *
	 * @see #getDriver()
	 * @see #setDriver(String)
	 *
	 * @since 1.0
	 */
	public String getAliasedDriver()
	{
		String driver = getDriver();
		if (null == driver)
		{
			return null;
		}

		String alias = sDriverAliases.get(driver);

		if (null == alias)
		{
			return driver;
		}

		return alias;
	}

	/**
	 * Sets the jdbc driver that will be used to connect to the database. This
	 * has to be a fully qualified class name and will be looked up through
	 * reflection. It's not possible to change the driver after a connection
	 * has been obtained from a pooled datasource.
	 * <p>
	 * If the class name can't be resolved, an exception is thrown during the
	 * creation of the first connection.
	 *
	 * @param driver a <code>String</code> with the fully qualified class name
	 * of the jdbc driver that will be used
	 *
	 * @see #getDriver()
	 *
	 * @since 1.0
	 */
	public void setDriver(String driver)
	{
		if (null == driver)						throw new IllegalArgumentException("driver can't be null.");
		if (0 == driver.length())				throw new IllegalArgumentException("driver can't be empty.");
		if (mConnectionPool.isInitialized())	throw new IllegalArgumentException("driver can't be changed after the connection pool has been set up.");

		mDriver = driver;
		mSqlConversion = null;
	}

	/**
	 * Retrieves the standard datasource that is used by this RIFE datasource
	 * to obtain a database connection.
	 *
	 * @return a standard <code>DataSource</code>; or
	 * <p>
	 * <code>null</code> if the standard datasource hasn't been set
	 *
	 * @see #setDataSource(DataSource)
	 *
	 * @since 1.3
	 */
    public DataSource getDataSource()
	{
        return mDataSource;
    }

	/**
	 * Sets the standard datasource that will be used to connect to the database.
	 *
	 * @param dataSource a standard <code>DataSource</code> that will be used
	 * by this RIFE datasource to obtain a database connection.
	 *
	 * @see #getDataSource()
	 *
	 * @since 1.0
	 */
    public void setDataSource(DataSource dataSource)
	{
        mDataSource = dataSource;
    }

	/**
	 * Retrieves the connection url that's used by this <code>Datasource</code>.
	 *
	 * @return a <code>String</code> with the connection url; or
	 * <p>
	 * <code>null</code> if the url hasn't been set
	 *
	 * @see #setUrl(String)
	 *
	 * @since 1.0
	 */
	public String getUrl()
	{
		return mUrl;
	}

	/**
	 * Sets the connection url that will be used to connect to the database.
	 * It's not possible to change the url after a connection has been obtained
	 * from a pooled datasource.
	 *
	 * @param url a <code>String</code> with the connection url that will be
	 * used
	 *
	 * @see #getUrl()
	 *
	 * @since 1.0
	 */
	public void setUrl(String url)
	{
		if (null == url)						throw new IllegalArgumentException("url can't be null.");
		if (0 == url.length())					throw new IllegalArgumentException("url can't be empty.");
		if (mConnectionPool.isInitialized())	throw new IllegalArgumentException("url can't be changed after the connection pool has been set up.");

		mUrl = url;
	}

	/**
	 * Retrieves the user that's used by this <code>Datasource</code>.
	 *
	 * @return a <code>String>/code> with the user; or
	 * <p>
	 * <code>null</code> if the user hasn't been set
	 *
	 * @see #setUser(String)
	 *
	 * @since 1.0
	 */
	public String getUser()
	{
		return mUser;
	}

	/**
	 * Sets the user that will be used to connect to the database.
	 * It's not possible to change the user after a connection has been obtained
	 * from a pooled datasource.
	 *
	 * @param user a <code>String</code> with the user that will be used
	 *
	 * @see #getUser()
	 *
	 * @since 1.0
	 */
	public void setUser(String user)
	{
		if (mConnectionPool.isInitialized())	throw new IllegalArgumentException("user can't be changed after the connection pool has been set up.");

		mUser = user;
	}

	/**
	 * Retrieves the password that's used by this <code>Datasource</code>.
	 *
	 * @return a <code>String>/code> with the password; or
	 * <p>
	 * <code>null</code> if the password hasn't been set
	 *
	 * @see #setPassword(String)
	 *
	 * @since 1.0
	 */
	public String getPassword()
	{
		return mPassword;
	}

	/**
	 * Sets the password that will be used to connect to the database.
	 * It's not possible to change the password after a connection has been
	 * obtained from a pooled datasource.
	 *
	 * @param password a <code>String</code> with the password that will be used
	 *
	 * @see #getPassword()
	 *
	 * @since 1.0
	 */
	public void setPassword(String password)
	{
		if (mConnectionPool.isInitialized())	throw new IllegalArgumentException("password can't be changed after the connection pool has been set up.");

		mPassword = password;
	}

	/**
	 * Retrieves the size of the pool that's used by this
	 * <code>Datasource</code>.
	 *
	 * @return a positive <code>int</code> with the size of the pool; or
	 * <p>
	 * <code>0</code> if no pool is being used
	 *
	 * @see #isPooled()
	 * @see #setPoolsize(int)
	 *
	 * @since 1.0
	 */
	public int getPoolsize()
	{
		return mConnectionPool.getPoolsize();
	}

	/**
	 * Indicates whether the <code>Datasource</code> uses a connection pool or
	 * not
	 *
	 * @return <code>true</code> if a pool is being used by this
	 * <code>Datasource</code>; or
	 * <p>
	 * <code>false</code> otherwise
	 *
	 * @see #getPoolsize()
	 * @see #setPoolsize(int)
	 *
	 * @since 1.0
	 */
	public boolean isPooled()
	{
		return getPoolsize() > 0;
	}

	/**
	 * Sets the size of the connection pool that will be used to connect to the
	 * database.
	 *
	 * @param poolsize a positive <code>int</code> with the size of the pool,
	 * providing <code>0</code> will disable the use of a connection pool for
	 * this <code>Datasource</code>.
	 *
	 * @see #getPoolsize()
	 * @see #isPooled()
	 *
	 * @since 1.0
	 */
	public void setPoolsize(int poolsize)
	{
		if (poolsize < 0)	throw new IllegalArgumentException("poolsize can't be negative.");

		mConnectionPool.setPoolsize(poolsize);
	}

	/**
	 * Retrieves the sql to java and java to sql type mapping logic that
	 * corresponds to the provide driver class name.
	 *
	 * @return a <code>SqlConversion</code> instance that is able to perform
	 * the required type conversions for the provided jdbc driver
	 *
	 * @throws UnsupportedJdbcDriverException when the provided jdbc isn't
	 * supported
	 *
	 * @since 1.0
	 */
	public SqlConversion getSqlConversion()
	throws UnsupportedJdbcDriverException
	{
		String driver = getDriver();
		if (null == mSqlConversion &&
			null != driver)
		{
			try
			{
				mSqlConversion = (SqlConversion)Class.forName("com.uwyn.rife.database.types.databasedrivers."+StringUtils.encodeClassname(getAliasedDriver())).newInstance();
			}
			catch (InstantiationException e)
			{
				throw new UnsupportedJdbcDriverException(driver, e);
			}
			catch (IllegalAccessException e)
			{
				throw new UnsupportedJdbcDriverException(driver, e);
			}
			catch (ClassNotFoundException e)
			{
				throw new UnsupportedJdbcDriverException(driver, e);
			}
		}

		return mSqlConversion;
	}

	/**
	 * Retrieves a <code>CapabilitiesCompensator</code> instance that is able to
	 * compensate for certain missing database features
	 *
	 * @return the requested <code>CapabilitiesCompensator</code> instance
	 *
	 * @throws UnsupportedJdbcDriverException when the provided jdbc isn't
	 * supported
	 *
	 * @since 1.0
	 */
	public CapabilitiesCompensator getCapabilitiesCompensator()
	throws UnsupportedJdbcDriverException
	{
		String driver = getDriver();
		if (null == mCapabilitiesCompensator &&
			null != driver)
		{
			try
			{
				mCapabilitiesCompensator = (CapabilitiesCompensator)Class.forName("com.uwyn.rife.database.capabilities."+StringUtils.encodeClassname(getAliasedDriver())).newInstance();
			}
			catch (InstantiationException e)
			{
				throw new UnsupportedJdbcDriverException(driver, e);
			}
			catch (IllegalAccessException e)
			{
				throw new UnsupportedJdbcDriverException(driver, e);
			}
			catch (ClassNotFoundException e)
			{
				throw new UnsupportedJdbcDriverException(driver, e);
			}
		}

		return mCapabilitiesCompensator;
	}

    /**
     * Returns a hash code value for the <code>Datasource</code>. This method is
     * supported for the benefit of hashtables such as those provided by
     * <code>java.util.Hashtable</code>.
	 *
	 * @return an <code>int</code> with the hash code value for this
	 * <code>Datasource</code>.
	 *
     * @see #equals(Object)
	 *
	 * @since 1.0
	 */
	public int hashCode()
	{
        int dataSourceHash = mDataSource == null ? 1 : mDataSource.hashCode();
        int driverHash = mDriver == null ? 1 : mDriver.hashCode ();
        int urlHash = mUrl == null ? 1 : mUrl.hashCode();
        int userHash = mUser == null ? 1 : mUser.hashCode();
        int passwordHash = mPassword == null ? 1 : mPassword.hashCode();
        return dataSourceHash * driverHash * urlHash * userHash * passwordHash;
	}

    /**
     * Indicates whether some other object is "equal to" this one. Only the
	 * real connection parameters will be taken into account. The size of the
	 * pool is not used for the comparison.
     *
     * @param object the reference object with which to compare.
     * @return <code>true</code> if this object is the same as the object
     * argument; or
	 * <p>
	 * <code>false</code> otherwise
	 *
     * @see #hashCode()
	 *
	 * @since 1.0
	 */
	public boolean equals(Object object)
	{
		if (this == object)
		{
			return true;
		}

		if (null == object)
		{
			return false;
		}

		if (!(object instanceof Datasource))
		{
			return false;
		}

		Datasource other_datasource = (Datasource)object;
		if (!other_datasource.getDriver().equals(getDriver()))
		{
			return false;
		}
		if (other_datasource.getUrl() != null || getUrl() != null)
		{
			if (null == other_datasource.getUrl() || null == getUrl())
			{
				return false;
			}
			if (!other_datasource.getUrl().equals(getUrl()))
			{
				return false;
			}
		}
		if (other_datasource.getDataSource() != null || getDataSource() != null)
		{
			if (null == other_datasource.getDataSource() || null == getDataSource())
			{
				return false;
			}
			if (!other_datasource.getDataSource().equals(getDataSource()))
			{
				return false;
			}
		}
		if (other_datasource.getUser() != null || getUser() != null)
		{
			if (null == other_datasource.getUser() || null == getUser())
			{
				return false;
			}
			if (!other_datasource.getUser().equals(getUser()))
			{
				return false;
			}
		}
		if (other_datasource.getPassword() != null || getPassword() != null)
		{
			if (null == other_datasource.getPassword() || null == getPassword())
			{
				return false;
			}
			if (!other_datasource.getPassword().equals(getPassword()))
			{
				return false;
			}
		}

		return true;
	}

	/**
	 * Simply clones the instance with the default clone method. This creates a
	 * shallow copy of all fields and the clone will in fact just be another
	 * reference to the same underlying data. The independence of each cloned
	 * instance is consciously not respected since they rely on resources
	 * that can't be cloned.
	 *
	 * @since 1.0
	 */
	public Datasource clone()
	{
		Datasource other = null;
		try
		{
			other = (Datasource)super.clone();
		}
		catch (CloneNotSupportedException e)
		{
			// this should never happen
			Logger.getLogger("com.uwyn.rife.database").severe(ExceptionUtils.getExceptionStackTrace(e));
			return null;
		}

		other.mSqlConversion = mSqlConversion;
		other.mConnectionPool = mConnectionPool;

		return other;
	}

	/**
	 * Cleans up all connections that have been reserved by this datasource.
	 *
	 * @throws DatabaseException when an error occured during the cleanup
	 *
	 * @since 1.0
	 */
	public void cleanup()
	throws DatabaseException
	{
		mConnectionPool.cleanup();
	}


	/**
	 * Retrieves the instance of the connection pool that is provided by this
	 * dtaasource.
	 *
	 * @return the requested instance of <code>ConnectionPool</code>
	 *
	 */
	public ConnectionPool getPool()
	{
		return mConnectionPool;
	}
}

