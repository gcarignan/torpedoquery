/**
 *
 *   Copyright 2011 Xavier Jodoin xjodoin@gmail.com
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package org.torpedoquery.jpa.test.bo;

public class ExtendEntity extends Entity {

	private String specificField;

	public String getSpecificField() {
		return specificField;
	}

	public void setSpecificField(String specificField) {
		this.specificField = specificField;
	}

	@Override
	public SubEntity getSubEntity() {
		return super.getSubEntity();
	}
	
	@Override
	protected void finalize() throws Throwable {
		super.finalize();
	}
}
