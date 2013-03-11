/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: TestSuiteDatabase.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package com.uwyn.rife.database;

import com.uwyn.rife.RifeTestSuite;
import junit.framework.Test;
import junit.framework.TestSuite;

public class TestSuiteDatabase extends TestSuite
{
	public static Test suite()
	{
		RifeTestSuite suite = new RifeTestSuite("Database API test suite");

		suite.addTestSuite(com.uwyn.rife.database.TestDatasource.class);
		suite.addTestSuite(com.uwyn.rife.database.TestDatasources.class);
		suite.addTestSuite(com.uwyn.rife.database.TestXml2Datasources.class);

		suite.addTestSuite(com.uwyn.rife.database.types.TestSqlArrays.class);
		suite.addTestSuite(com.uwyn.rife.database.types.TestCommon.class);

		suite.addDatasourcedTestSuite(com.uwyn.rife.database.TestDbConnection.class);
		suite.addDatasourcedTestSuite(com.uwyn.rife.database.TestDbStatement.class);
		suite.addDatasourcedTestSuite(com.uwyn.rife.database.TestDbPreparedStatement.class);
		suite.addDatasourcedTestSuite(com.uwyn.rife.database.TestDbBeanFetcher.class);
		suite.addDatasourcedTestSuite(com.uwyn.rife.database.TestDbQueryManager.class);
		suite.addDatasourcedTestSuite(com.uwyn.rife.database.TestDbQueryManagerFactory.class);
		suite.addDatasourcedTestSuite(com.uwyn.rife.database.TestCapabilities.class);
		suite.addDatasourcedTestSuite(com.uwyn.rife.database.TestDbConcurrency.class);

		if (Datasources.getRepInstance().getDatasource("unittestspgsql") != null)
		{
			suite.addTestSuite(com.uwyn.rife.database.types.Test_org_postgresql_Driver.class);
			suite.addTestSuite(com.uwyn.rife.database.queries.TestCreateSequencePgsql.class);
			suite.addTestSuite(com.uwyn.rife.database.queries.TestDropSequencePgsql.class);
			suite.addTestSuite(com.uwyn.rife.database.queries.TestSequenceValuePgsql.class);
			suite.addTestSuite(com.uwyn.rife.database.queries.TestCreateTablePgsql.class);
			suite.addTestSuite(com.uwyn.rife.database.queries.TestDropTablePgsql.class);
			suite.addTestSuite(com.uwyn.rife.database.queries.TestSelectPgsql.class);
			suite.addTestSuite(com.uwyn.rife.database.queries.TestInsertPgsql.class);
			suite.addTestSuite(com.uwyn.rife.database.queries.TestUpdatePgsql.class);
			suite.addTestSuite(com.uwyn.rife.database.queries.TestDeletePgsql.class);
		}
		else
		{
			System.out.println("WARNING : PostgreSQL query tests not executed");
		}

		if (Datasources.getRepInstance().getDatasource("unittestsmysql") != null)
		{
			suite.addTestSuite(com.uwyn.rife.database.types.Test_com_mysql_jdbc_Driver.class);
			suite.addTestSuite(com.uwyn.rife.database.queries.TestCreateSequenceMysql.class);
			suite.addTestSuite(com.uwyn.rife.database.queries.TestDropSequenceMysql.class);
			suite.addTestSuite(com.uwyn.rife.database.queries.TestSequenceValueMysql.class);
			suite.addTestSuite(com.uwyn.rife.database.queries.TestCreateTableMysql.class);
			suite.addTestSuite(com.uwyn.rife.database.queries.TestDropTableMysql.class);
			suite.addTestSuite(com.uwyn.rife.database.queries.TestSelectMysql.class);
			suite.addTestSuite(com.uwyn.rife.database.queries.TestInsertMysql.class);
			suite.addTestSuite(com.uwyn.rife.database.queries.TestUpdateMysql.class);
			suite.addTestSuite(com.uwyn.rife.database.queries.TestDeleteMysql.class);
		}
		else
		{
			System.out.println("WARNING : MySQL query tests not executed");
		}

		if (Datasources.getRepInstance().getDatasource("unittestsoracle") != null)
		{
			suite.addTestSuite(com.uwyn.rife.database.types.Test_oracle_jdbc_driver_OracleDriver.class);
			suite.addTestSuite(com.uwyn.rife.database.queries.TestCreateSequenceOracle.class);
			suite.addTestSuite(com.uwyn.rife.database.queries.TestDropSequenceOracle.class);
			suite.addTestSuite(com.uwyn.rife.database.queries.TestSequenceValueOracle.class);
			suite.addTestSuite(com.uwyn.rife.database.queries.TestCreateTableOracle.class);
			suite.addTestSuite(com.uwyn.rife.database.queries.TestDropTableOracle.class);
			suite.addTestSuite(com.uwyn.rife.database.queries.TestSelectOracle.class);
			suite.addTestSuite(com.uwyn.rife.database.queries.TestInsertOracle.class);
			suite.addTestSuite(com.uwyn.rife.database.queries.TestUpdateOracle.class);
			suite.addTestSuite(com.uwyn.rife.database.queries.TestDeleteOracle.class);
		}
		else
		{
			System.out.println("WARNING : Oracle query tests not executed");
		}

		if (Datasources.getRepInstance().getDatasource("unittestshsqldb") != null)
		{
			suite.addTestSuite(com.uwyn.rife.database.types.Test_org_hsqldb_jdbcDriver.class);
			suite.addTestSuite(com.uwyn.rife.database.queries.TestCreateSequenceHsqldb.class);
			suite.addTestSuite(com.uwyn.rife.database.queries.TestDropSequenceHsqldb.class);
			suite.addTestSuite(com.uwyn.rife.database.queries.TestSequenceValueHsqldb.class);
			suite.addTestSuite(com.uwyn.rife.database.queries.TestCreateTableHsqldb.class);
			suite.addTestSuite(com.uwyn.rife.database.queries.TestDropTableHsqldb.class);
			suite.addTestSuite(com.uwyn.rife.database.queries.TestSelectHsqldb.class);
			suite.addTestSuite(com.uwyn.rife.database.queries.TestInsertHsqldb.class);
			suite.addTestSuite(com.uwyn.rife.database.queries.TestUpdateHsqldb.class);
			suite.addTestSuite(com.uwyn.rife.database.queries.TestDeleteHsqldb.class);
		}
		else
		{
			System.out.println("WARNING : HypersonicSQL query tests not executed");
		}

		if (Datasources.getRepInstance().getDatasource("unittestsh2") != null)
		{
			suite.addTestSuite(com.uwyn.rife.database.types.Test_org_h2_Driver.class);
			suite.addTestSuite(com.uwyn.rife.database.queries.TestCreateSequenceH2.class);
			suite.addTestSuite(com.uwyn.rife.database.queries.TestDropSequenceH2.class);
			suite.addTestSuite(com.uwyn.rife.database.queries.TestSequenceValueH2.class);
			suite.addTestSuite(com.uwyn.rife.database.queries.TestCreateTableH2.class);
			suite.addTestSuite(com.uwyn.rife.database.queries.TestDropTableH2.class);
			suite.addTestSuite(com.uwyn.rife.database.queries.TestSelectH2.class);
			suite.addTestSuite(com.uwyn.rife.database.queries.TestInsertH2.class);
			suite.addTestSuite(com.uwyn.rife.database.queries.TestUpdateH2.class);
			suite.addTestSuite(com.uwyn.rife.database.queries.TestDeleteH2.class);
		}
		else
		{
			System.out.println("WARNING : H2 query tests not executed");
		}

		if (Datasources.getRepInstance().getDatasource("unittestsfirebird") != null)
		{
			suite.addTestSuite(com.uwyn.rife.database.types.Test_org_firebirdsql_jdbc_FBDriver.class);
			suite.addTestSuite(com.uwyn.rife.database.queries.TestCreateSequenceFirebird.class);
			suite.addTestSuite(com.uwyn.rife.database.queries.TestDropSequenceFirebird.class);
			suite.addTestSuite(com.uwyn.rife.database.queries.TestSequenceValueFirebird.class);
			suite.addTestSuite(com.uwyn.rife.database.queries.TestCreateTableFirebird.class);
			suite.addTestSuite(com.uwyn.rife.database.queries.TestDropTableFirebird.class);
			suite.addTestSuite(com.uwyn.rife.database.queries.TestSelectFirebird.class);
			suite.addTestSuite(com.uwyn.rife.database.queries.TestInsertFirebird.class);
			suite.addTestSuite(com.uwyn.rife.database.queries.TestUpdateFirebird.class);
			suite.addTestSuite(com.uwyn.rife.database.queries.TestDeleteFirebird.class);
		}
		else
		{
			System.out.println("WARNING : Firebird query tests not executed");
		}

		if (Datasources.getRepInstance().getDatasource("unittestsmckoi") != null)
		{
			suite.addTestSuite(com.uwyn.rife.database.types.Test_com_mckoi_JDBCDriver.class);
			suite.addTestSuite(com.uwyn.rife.database.queries.TestCreateSequenceMckoi.class);
			suite.addTestSuite(com.uwyn.rife.database.queries.TestDropSequenceMckoi.class);
			suite.addTestSuite(com.uwyn.rife.database.queries.TestSequenceValueMckoi.class);
			suite.addTestSuite(com.uwyn.rife.database.queries.TestCreateTableMckoi.class);
			suite.addTestSuite(com.uwyn.rife.database.queries.TestDropTableMckoi.class);
			suite.addTestSuite(com.uwyn.rife.database.queries.TestSelectMckoi.class);
			suite.addTestSuite(com.uwyn.rife.database.queries.TestInsertMckoi.class);
			suite.addTestSuite(com.uwyn.rife.database.queries.TestUpdateMckoi.class);
			suite.addTestSuite(com.uwyn.rife.database.queries.TestDeleteMckoi.class);
		}
		else
		{
			System.out.println("WARNING : McKoiSQL query tests not executed");
		}

		if (Datasources.getRepInstance().getDatasource("unittestsderby") != null)
		{
			suite.addTestSuite(com.uwyn.rife.database.types.Test_org_apache_derby_jdbc_EmbeddedDriver.class);
			suite.addTestSuite(com.uwyn.rife.database.queries.TestCreateSequenceDerby.class);
			suite.addTestSuite(com.uwyn.rife.database.queries.TestDropSequenceDerby.class);
			suite.addTestSuite(com.uwyn.rife.database.queries.TestSequenceValueDerby.class);
			suite.addTestSuite(com.uwyn.rife.database.queries.TestCreateTableDerby.class);
			suite.addTestSuite(com.uwyn.rife.database.queries.TestDropTableDerby.class);
			suite.addTestSuite(com.uwyn.rife.database.queries.TestSelectDerby.class);
			suite.addTestSuite(com.uwyn.rife.database.queries.TestInsertDerby.class);
			suite.addTestSuite(com.uwyn.rife.database.queries.TestUpdateDerby.class);
			suite.addTestSuite(com.uwyn.rife.database.queries.TestDeleteDerby.class);
		}
		else
		{
			System.out.println("WARNING : Derby query tests not executed");
		}

		if (Datasources.getRepInstance().getDatasource("unittestsdaffodil") != null)
		{
			suite.addTestSuite(com.uwyn.rife.database.types.Test_in_co_daffodil_db_jdbc_DaffodilDBDriver.class);
			suite.addTestSuite(com.uwyn.rife.database.queries.TestCreateSequenceDaffodil.class);
			suite.addTestSuite(com.uwyn.rife.database.queries.TestDropSequenceDaffodil.class);
			suite.addTestSuite(com.uwyn.rife.database.queries.TestSequenceValueDaffodil.class);
			suite.addTestSuite(com.uwyn.rife.database.queries.TestCreateTableDaffodil.class);
			suite.addTestSuite(com.uwyn.rife.database.queries.TestDropTableDaffodil.class);
			suite.addTestSuite(com.uwyn.rife.database.queries.TestSelectDaffodil.class);
			suite.addTestSuite(com.uwyn.rife.database.queries.TestInsertDaffodil.class);
			suite.addTestSuite(com.uwyn.rife.database.queries.TestUpdateDaffodil.class);
			suite.addTestSuite(com.uwyn.rife.database.queries.TestDeleteDaffodil.class);
		}
		else
		{
			System.out.println("WARNING : DaffodilDB query tests not executed");
		}

		suite.addDatasourcedTestSuite(com.uwyn.rife.database.querymanagers.generic.TestGenericQueryManagerSimple.class);
		suite.addDatasourcedTestSuite(com.uwyn.rife.database.querymanagers.generic.TestGenericQueryManagerBinary.class);
		suite.addDatasourcedTestSuite(com.uwyn.rife.database.querymanagers.generic.TestGenericQueryManagerConstrained.class);
		suite.addDatasourcedTestSuite(com.uwyn.rife.database.querymanagers.generic.TestGenericQueryManagerManyToOne.class);
		suite.addDatasourcedTestSuite(com.uwyn.rife.database.querymanagers.generic.TestGenericQueryManagerManyToMany.class);
		suite.addDatasourcedTestSuite(com.uwyn.rife.database.querymanagers.generic.TestGenericQueryManagerChild.class);
		suite.addDatasourcedTestSuite(com.uwyn.rife.database.querymanagers.generic.TestGenericQueryManagerUnique.class);
		suite.addDatasourcedTestSuite(com.uwyn.rife.database.querymanagers.generic.TestGenericQueryManagerCallbacks.class);
		suite.addDatasourcedTestSuite(com.uwyn.rife.database.querymanagers.generic.TestGenericQueryManagerDelegate.class);
		suite.addDatasourcedTestSuite(com.uwyn.rife.database.querymanagers.generic.TestRestoreQuery.class);
		suite.addDatasourcedTestSuite(com.uwyn.rife.database.querymanagers.generic.TestCountQuery.class);
		suite.addDatasourcedTestSuite(com.uwyn.rife.database.querymanagers.generic.TestDeleteQuery.class);

		return suite;
	}
}
