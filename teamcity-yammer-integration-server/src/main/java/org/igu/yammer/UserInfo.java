/*
 * Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.igu.yammer;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 * Represents a user
 * 
 */
public class UserInfo {

	private String id;
	private String email;
	private String fullName;
	private String jobTitle;
	private String location;
	private String imProvider;
	private String imUsername;
	private String workTelephone;
	private String workExtension;
	private String mobile;
	private String externalProfiles;
	private String significantOther;
	private String kidsNames;
	private String interests;
	private String summary;
	private String expertise;


	public UserInfo(String id, String email, String fullName, String jobTitle, String location, String imProvider, String imUsername,
			String workTelephone, String workExtension, String mobile, String externalProfiles, String significantOther, String kidsNames,
			String interests, String summary, String expertise) {
		this.id = id;
		this.email = email;
		this.fullName = fullName;
		this.jobTitle = jobTitle;
		this.location = location;
		this.imProvider = imProvider;
		this.imUsername = imUsername;
		this.workTelephone = workTelephone;
		this.workExtension = workExtension;
		this.mobile = mobile;
		this.externalProfiles = externalProfiles;
		this.significantOther = significantOther;
		this.kidsNames = kidsNames;
		this.interests = interests;
		this.summary = summary;
		this.expertise = expertise;
	}


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getJobTitle() {
		return jobTitle;
	}

	public void setJobTitle(String jobTitle) {
		this.jobTitle = jobTitle;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getImProvider() {
		return imProvider;
	}

	public void setImProvider(String imProvider) {
		this.imProvider = imProvider;
	}

	public String getImUsername() {
		return imUsername;
	}

	public void setImUsername(String imUsername) {
		this.imUsername = imUsername;
	}

	public String getWorkTelephone() {
		return workTelephone;
	}

	public void setWorkTelephone(String workTelephone) {
		this.workTelephone = workTelephone;
	}

	public String getWorkExtension() {
		return workExtension;
	}

	public void setWorkExtension(String workExtension) {
		this.workExtension = workExtension;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getExternalProfiles() {
		return externalProfiles;
	}

	public void setExternalProfiles(String externalProfiles) {
		this.externalProfiles = externalProfiles;
	}

	public String getSignificantOther() {
		return significantOther;
	}

	public void setSignificantOther(String significantOther) {
		this.significantOther = significantOther;
	}

	public String getKidsNames() {
		return kidsNames;
	}

	public void setKidsNames(String kidsNames) {
		this.kidsNames = kidsNames;
	}

	public String getInterests() {
		return interests;
	}

	public void setInterests(String interests) {
		this.interests = interests;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getExpertise() {
		return expertise;
	}

	public void setExpertise(String expertise) {
		this.expertise = expertise;
	}
}
