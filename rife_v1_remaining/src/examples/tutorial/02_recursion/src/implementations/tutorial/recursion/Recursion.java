/*
 * Copyright 2001-2008 Geert Bevin <gbevin[remove] at uwyn dot com>
 * Licensed under the Apache License, Version 2.0 (the "License")
 * $Id: Recursion.java 3918 2008-04-14 17:35:35Z gbevin $
 */
package tutorial.recursion;

import com.uwyn.rife.engine.Element;
import com.uwyn.rife.template.InternalValue;
import com.uwyn.rife.template.Template;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Demonstrates how the template engine can be used to construct complex
 * recursive content through the use of <code>InternalValue</code> objects and
 * internal template construction.
 *
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @version $Revision: 3918 $
 */
public class Recursion extends Element {
	/**
	 * The element's entry point.
	 */
	public void processElement() {
		/* Generate a tree with the following structure :
		 *
		 * +- node1
		 * |   |
		 * |   +- node1a
		 * |   +- node1b
		 * |   +- node1c
		 * |
		 * +- node2
		 * |   |
		 * |   +- node2a
		 * |   |   |
		 * |   |   +- node2a1
		 * |   |   +- node2a2
		 * |   |
		 * |   +- node2b
		 * |
		 * +- node3
		 *     |
		 *     +- node3a
		 *     +- node3b
		 */
		TreeNode tree = new TreeNode();
		TreeNode node1 = new TreeNode(tree, "node1");
		TreeNode node2 = new TreeNode(tree, "node2");
		TreeNode node3 = new TreeNode(tree, "node3");
		new TreeNode(node1, "node1a");
		new TreeNode(node1, "node1b");
		new TreeNode(node1, "node1c");
		TreeNode node2a = new TreeNode(node2, "node2a");
		new TreeNode(node2, "node2b");
		new TreeNode(node3, "node3a");
		new TreeNode(node3, "node3b");
		new TreeNode(node2a, "node2a1");
		new TreeNode(node2a, "node2a2");
		
		// obtain an instance of the template that will output the tree
		Template template = getHtmlTemplate("tutorial.recursion");
		
		// process the tree for output to the template
		tree.output(template);
		
		// print the template
		print(template);
	}
	
	/**
	 * A node in the tree structure that will output itself to a provided
	 * template instance.
	 *
	 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
	 */
	class TreeNode {
		private String		title = null;
		private ArrayList	children = new ArrayList();
		private TreeNode	parent = null;
		
		/**
		 * Constructs the root node from which all others will branch.
		 */
		public TreeNode() {}
		
		/**
		 * Add a child node to the provided parent node.
		 * Each child has a mandatory <code>title</code>.
		 *
		 * @param parent the parent node
		 * @param title  the title of the child node
		 *
		 */
		public TreeNode(TreeNode parent, String title) {
			if (null == title) throw new IllegalArgumentException("title can't be null.");
			
			parent.addChild(this);
			this.title = title;
		}
		
		/**
		 * Outputs the tree structure to a provided <code>Template</code>
		 * instance.
		 * <p>
		 * Each node will be appended to the <code>nodes</code> value and obtain
		 * from the <code>node</code> block. This block contains a
		 * <code>title</code> value that will contain the title of the node.
		 * <p>
		 * Each new depth level will be obtained from the <code>level</code>
		 * block and assigned to the <code>level</code> value. This value
		 * contains the <code>nodes</code> value to output a collection of
		 * sibling nodes and the <code>level</code> is present in the
		 * <code>node</code> block to allow child nodes to be inserted.
		 * <p>
		 * The complete tree is displayed by a top-level <code>level</code>
		 * value that will contain all first-level nodes, who will thus in their
		 * turn contain their own direct child nodes, and so on ...
		 *
		 * @param  template the <code>Template</code> instance in which the
		 * tree structure should be output
		 *
		 */
		public void output(Template template) {
			// if no children are present, clear the level value
			if (0 == children.size()) {
				template.setValue("level", "");
			} else {
				// obtain a new internal value to construct a collection
				// of sibling child nodes in the local scope
				InternalValue	nodes = template.createInternalValue();
				
				// iterete over the children
				Iterator	child_it = children.iterator();
				TreeNode	child = null;
				while (child_it.hasNext()) {
					// obtain the next child
					child = (TreeNode)child_it.next();
					// and output it before the processing continues
					child.output(template);
					// set the child's title value
					template.setValue("title", encodeHtml(child.getTitle()));
					// and append it to the local internal value
					nodes.appendBlock("node");
				}
				// when all children have been processed, set to nodes value
				// to the locally constructed internal value
				template.setValue("nodes", nodes);
				// set the level value which includes the sibling nodes in the
				// same level
				template.setBlock("level", "level");
			}
		}
		
		/**
		 * Adds a child to the current <code>TreeNode</code> instance.
		 *
		 * @param child the child <code>TreeNode</code>
		 *
		 */
		private void addChild(TreeNode child) {
			child.parent = this;
			children.add(child);
		}
		
		/**
		 * Retrieves the title of the current <code>TreeNode</code> instance.
		 *
		 * @return a <code>String</code> instance with the title; or
		 * <p>
		 * <code>null</code> if this is the root node.
		 */
		public String getTitle() {
			return title;
		}
		
		/**
		 * Retrieves the parent of the current <code>TreeNode</code> instance.
		 *
		 * @return a <code>TreeNode</code> instance with the parent; or
		 * <p>
		 * <code>null</code> if this is the root node.
		 */
		public TreeNode getParent() {
			return parent;
		}
	}
}
