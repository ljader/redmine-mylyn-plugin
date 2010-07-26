package net.sf.redmine_mylyn.internal.api.client;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


@Retention(RetentionPolicy.RUNTIME)
public @interface IssuePropertyMapping {

	RedmineApiIssueProperty value();
	
}
