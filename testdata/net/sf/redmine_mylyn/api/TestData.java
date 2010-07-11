package net.sf.redmine_mylyn.api;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import net.sf.redmine_mylyn.api.model.Attachment;
import net.sf.redmine_mylyn.api.model.Configuration;
import net.sf.redmine_mylyn.api.model.CustomField;
import net.sf.redmine_mylyn.api.model.CustomField.Format;
import net.sf.redmine_mylyn.api.model.CustomField.Type;
import net.sf.redmine_mylyn.api.model.CustomValue;
import net.sf.redmine_mylyn.api.model.Issue;
import net.sf.redmine_mylyn.api.model.Journal;
import net.sf.redmine_mylyn.api.model.Project;
import net.sf.redmine_mylyn.api.model.Property;
import net.sf.redmine_mylyn.api.model.Settings;
import net.sf.redmine_mylyn.api.model.TimeEntry;
import net.sf.redmine_mylyn.api.model.User;
import net.sf.redmine_mylyn.api.model.container.AbstractPropertyContainer;
import net.sf.redmine_mylyn.api.model.container.AbstractTypedContainer;
import net.sf.redmine_mylyn.api.model.container.Attachments;
import net.sf.redmine_mylyn.api.model.container.CustomFields;
import net.sf.redmine_mylyn.api.model.container.CustomValues;
import net.sf.redmine_mylyn.api.model.container.Journals;
import net.sf.redmine_mylyn.api.model.container.Projects;
import net.sf.redmine_mylyn.api.model.container.TimeEntries;
import net.sf.redmine_mylyn.api.model.container.Users;

public class TestData {

	static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	
	public final static Configuration cfg = buildConfiguration();
	
	public final static Issue issue2 = buildIssue2();
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	static Issue buildIssue2() {
		try {
			Issue issue = new Issue();
			
			Field idField = Issue.class.getDeclaredField("id");
			idField.setAccessible(true);
			idField.setInt(issue, 2);
			
			issue.setSubject("Add ingredients categories");
			issue.setDescription("Ingredients of the recipe should be classified by categories");
			issue.setCreatedOn(new Date(1153335861000l));
			issue.setUpdatedOn(new Date(1153336190000l));
			issue.setTrackerId(2);
			issue.setProjectId(1);
			issue.setStatusId(2);
			issue.setPriorityId(5);
			issue.setWatched(true);
			issue.setWatcherIds(new int[]{1,3});
			issue.setStartDate(df.parse("2010-05-08"));
			//dueDate
			issue.setDoneRatio(10);
	 		issue.setEstimatedHours(3.5f);
			issue.setAuthorId(2);
			//categoryId
			issue.setAssignedToId(3);
			issue.setFixedVersionId(2);
			issue.setParentId(1);
			
			issue.setAvailableStatusId(new int[]{1,3,4,5,6,2});
			
			/* CustomValues */
			issue.setCustomValues(new CustomValues());
			List lst = getList(issue.getCustomValues());
			lst.add(buildCustomValue(5, 2, null));
			lst.add(buildCustomValue(11, 6, "2.05"));
			lst.add(buildCustomValue(12, 1, "Oracle"));
			lst.add(buildCustomValue(13, 9, "2009-12-01"));
			
			/* Journals */
			issue.setJournals(new Journals());
			lst = getList(issue.getJournals()); 
			lst.add(buildJournal(3, 2, 1273356000000l, "A comment with inline image: !picture.jpg!"));
			
			/* Attachments */
			issue.setAttachments(new Attachments());
			lst = getList(issue.getAttachments());
			lst.add(buildAttachment(10, 2, 1153336047000l, "picture.jpg", 452, "b91e08d0cf966d5c6ff411bd8c4cc3a2", "image/jpeg", "kurze Beschreibung"));
			
			/* IssueRelations */
			
			/* TimeEntries + sum*/
			TimeEntries timeEntries = new TimeEntries();
			Field sum = timeEntries.getClass().getDeclaredField("sum");
			sum.setAccessible(true);
			sum.setFloat(timeEntries, 4.25f);
			issue.setTimeEntries(timeEntries);
			lst = getList(timeEntries);
			lst.add(buildTimeEntry(1, 4.25f, 9, 2, "2007-03-23", "My hours", buildCustomValue(5, 7, "1")));
			
			return issue;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	static Configuration buildConfiguration() {
		try {
			Configuration cfg = new Configuration();
			cfg.setSettings(new Settings());
			
			buildCustomFields(cfg.getCustomFields());
//		cfg.getIssueCategories()
//		cfg.getIssuePriorities()
//		cfg.getIssueStatuses()
			buildProjects(cfg.getProjects());
//		cfg.getQueries()
//		cfg.getSettings()
//		cfg.getTimeEntryActivities()
//		cfg.getTrackers()
			buildUsers(cfg.getUsers());
//		cfg.getVersions()
			return cfg;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	static void buildProjects(Projects ct) throws Exception {
		@SuppressWarnings("rawtypes")
		List lst = getCollection(ct);
		lst.add(buildProject(1, "eCookbook", "ecookbook", new int[]{1,2,3}, new int[]{1,2,3,4,6,7}, new int[]{1,2}));
	}
	
	static Project buildProject(int id, String name, String identifier, int[] trackers, int[] versions, int[] categories) throws Exception {
		Project project = new Project();
		setId(project, id);
		project.setName(name);
		project.setIdentifier(identifier);
		project.setTrackerIds(trackers);
		project.setVersionIds(versions);
		project.setIssueCategoryIds(categories);
		
		//TODO member
		//TODO issueCustomFields
		
		return project;
	}
	
	@SuppressWarnings("unchecked")
	static void buildUsers(Users ct) throws Exception {
		@SuppressWarnings("rawtypes")
		List lst = getCollection(ct);
		lst.add(buildUser(1, "redMine", "Admin", "redMine Admin", "admin", "admin@somenet.foo"));
		lst.add(buildUser(2, "John", "Smith", "John Smith", "jsmith", "jsmith@somenet.foo"));
		lst.add(buildUser(3, "Dave", "Lopper", "Dave Lopper", "dlopper", "dlopper@somenet.foo"));
		lst.add(buildUser(4, "Robert", "Hill", "Robert Hill", "rhill", "rhill@somenet.foo"));
	}
	
	static User buildUser(int id, String fname, String lname, String name, String login, String mail) throws Exception {
		User user = new User();
		setId(user, id);
		user.setFirstname(fname);
		user.setLastname(lname);
		user.setName(name);
		user.setLogin(login);
		user.setMail(mail);
		return user;
	}
	
	@SuppressWarnings("unchecked")
	static void buildCustomFields(CustomFields ct) throws Exception {
		@SuppressWarnings("rawtypes")
		List lst = getCollection(ct);
		lst.add(buildCustomField(1, "Database", Type.IssueCustomField, Format.LIST, 0, 0, null, null, false, true, true, "MySQL", "PostgreSQL", "Oracle"));
		lst.add(buildCustomField(2, "Searchable field", Type.IssueCustomField, Format.STRING, 1, 100, "^.*$", "Default String", false, false, true));
		lst.add(buildCustomField(5, "Money", Type.IssueCustomField, Format.FLOAT, 0, 0, null, null, false, false, false));
		lst.add(buildCustomField(6, "Float field", Type.IssueCustomField, Format.FLOAT, 0, 0, null, null, false, false, true));
		lst.add(buildCustomField(7, "Billable", Type.TimeEntryActivityCustomField, Format.BOOL, 0, 0, null, null, false, true, false));
		lst.add(buildCustomField(8, "Custom date", Type.IssueCustomField, Format.DATE, 0, 0, null, null, false, false, true));
		lst.add(buildCustomField(9, "Project 1 cf", Type.IssueCustomField, Format.DATE, 0, 0, null, null, false, true, false));
		//TODO
	}
	
	static CustomField buildCustomField(int id, String name, Type type, Format format, int min, int max, String regex, String defValue, boolean isRequired, boolean isFilter, boolean isForAll, String... options) throws Exception {
		CustomField cf = new CustomField();
		setId(cf, id);
		cf.setName(name);
		cf.setType(type);
		cf.setFieldFormat(format);
		cf.setMinLength(min);
		cf.setMaxLength(max);
		cf.setRegexp(regex);
		cf.setPossibleValues(Arrays.asList(options));
		cf.setDefaultValue(defValue);
		cf.setRequired(isRequired);
		cf.setFilter(isFilter);
		cf.setForAll(isForAll);
		return cf;
	}
	
	static CustomValue buildCustomValue(int id, int customFieldId, String value) throws Exception {
		CustomValue customValue = new CustomValue();
		setId(customValue, id);
		customValue.setCustomFieldId(customFieldId);
		customValue.setValue(value);
		return customValue;
	}
	
	static Journal buildJournal(int id, int userId, long createdOn, String notes) throws Exception {
		Journal journal = new Journal();
		setId(journal, id);
		journal.setUserId(userId);
		journal.setCreatedOn(new Date(createdOn));
		journal.setNotes(notes);
		return journal;
	}
	
	static Attachment buildAttachment(int id, int authorId, long createdOn, String filename, int filesize, String digest, String contentType, String description) throws Exception {
		Attachment attachment = new Attachment();
		setId(attachment, id);
		attachment.setAuthorId(authorId);
		attachment.setCreatedOn(new Date(createdOn));
		attachment.setFilename(filename);
		attachment.setFilesize(filesize);
		attachment.setDigest(digest);
		attachment.setDescription(description);
		return attachment;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	static TimeEntry buildTimeEntry(int id, float hours, int activityId, int userId, String spentOn, String comments, CustomValue... values) throws Exception {
		TimeEntry te = new TimeEntry();
		setId(te, id);
		te.setHours(hours);
		te.setActivityId(activityId);
		te.setUserId(userId);
		te.setSpentOn(df.parse(spentOn));
		te.setComments(comments);
		te.setCustomValues(new CustomValues());
		List lst = getList(te.getCustomValues());
		for (CustomValue v : values) {
			lst.add(v);
		}
		return te;
	}
	
	static void setId(Object obj, int id) throws Exception {
		Field f = null;
		if( obj instanceof Property) {
			f = Property.class.getDeclaredField("id");
		} else {
			f = obj.getClass().getDeclaredField("id");
		}
		f.setAccessible(true);
		f.setInt(obj, id);
	}
	
	@SuppressWarnings("rawtypes")
	static List<?> getList(AbstractTypedContainer<?> ct) throws Exception {
		Method m = ct.getClass().getDeclaredMethod("getModifiableList");
		m.setAccessible(true);
		return (List)m.invoke(ct);
	}
	
	@SuppressWarnings("rawtypes")
	static List<?> getCollection(AbstractPropertyContainer<?> ct) throws Exception {
		Method m = ct.getClass().getDeclaredMethod("getModifiableList");
		m.setAccessible(true);
		return (List)m.invoke(ct);
	}
	
}
