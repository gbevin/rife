/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: FriendManager.java 3943 2008-04-27 09:09:02Z gbevin $
 */
package tutorial.friends.backend;

import com.uwyn.rife.config.Config;
import com.uwyn.rife.database.Datasource;
import com.uwyn.rife.database.Datasources;
import com.uwyn.rife.database.DbPreparedStatement;
import com.uwyn.rife.database.DbPreparedStatementHandler;
import com.uwyn.rife.database.DbQueryManager;
import com.uwyn.rife.database.DbRowProcessor;
import com.uwyn.rife.database.exceptions.DatabaseException;
import com.uwyn.rife.database.queries.CreateTable;
import com.uwyn.rife.database.queries.DropTable;
import com.uwyn.rife.database.queries.Insert;
import com.uwyn.rife.database.queries.Select;

/**
 * Manages a list of visitors that are stored in a database.
 *
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3943 $
 */
public class FriendManager {
	private static String	TABLE_NAME_FRIEND = "friend";
	
	private final DbQueryManager manager;
	
	public FriendManager() {
		this.manager = new DbQueryManager(Datasources.getRepInstance().getDatasource(Config.getRepInstance().getString("DATASOURCE")));
	}
	
	public Datasource getDatasource() {
		return manager.getDatasource();
	}
	
	/**
	 * Installs and populates the database structure.
	 *
	 * @throws DatabaseException when a database access error occurs.
	 */
	public void install() {
		CreateTable create = new CreateTable(manager.getDatasource())
			.table(TABLE_NAME_FRIEND)
			.columns(Friend.class);
		manager.executeUpdate(create);
		
		add(new Friend("JR", "Boyens", "Partner in RIFE crime", "http://rifers.org/blogs/jboyens"));
		add(new Friend("Steph", "Meslin-Weber", "Zaurus Java FAQ, Wonka Java VM, QT/embedded AWT Java bindings", "http://adorphuye.com"));
		add(new Friend("Eugene", "Ciurana", "Author, Sushi expert, Muay Thai, #java IRC channel pastebin, variety of tools", "http://eugeneciurana.com"));
		add(new Friend("Mikael", "Hallendal", "GNOME, Loudmoudth, Gossip, MrPoject, Devhelp, Yelp", "http://www.imendio.com"));
		add(new Friend("Paul", "Fleischer", "PapuaWM - a minimalistic windowmanager", "http://www.papuaos.org/papuawm"));
		add(new Friend("Mike", "Newman", "gnome-pkgview, gtkdial, gwvedit, gpuce, LemmingChat", "http://www.greatnorthern.demon.co.uk/packages/"));
		add(new Friend("Lasse", "Bang Mikkelsen", "PapuaWEB - a php page publishing environment with authentication.", "http://www.papuaos.org/papuaweb"));
	}
	
	/**
	 * Adds data of a <code>Friend</code> instance to the database.
	 *
	 * @param friend the <code>Friend</code> that will be added to the database
	 *
	 * @throws DatabaseException when a database access error occurs
	 *
	 */
	public void add(final Friend friend) {
		Insert insert = new Insert(manager.getDatasource())
			.into(TABLE_NAME_FRIEND)
			.fieldsParameters(Friend.class);
		
		manager.executeUpdate(insert, new DbPreparedStatementHandler() {
				public void setParameters(DbPreparedStatement statement) {
					statement
						.setBean(friend);
				}
			});
	}
	
	/**
	 * Display the data in the database.
	 *
	 * @param processor the <code>DbRowProcessor</code> that will process the
	 * results
	 *
	 * @throws DatabaseException when a database access error occurs
	 */
	public void display(DbRowProcessor processor) {
		Select select = new Select(manager.getDatasource())
			.from(TABLE_NAME_FRIEND)
			.fields(Friend.class)
			.orderBy("firstname");
		
		// fetch every row in the resultset and forward the processing
		// of the data to the DisplayProcessor
		manager.executeFetchAll(select, processor);
	}
	
	/**
	 * Removes the database structure.
	 *
	 * @throws DatabaseException when a database access error occurs.
	 */
	public void remove() {
		DropTable drop = new DropTable(manager.getDatasource())
			.table(TABLE_NAME_FRIEND);
		manager.executeUpdate(drop);
	}
}

