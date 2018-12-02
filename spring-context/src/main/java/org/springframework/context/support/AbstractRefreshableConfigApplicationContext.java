/*
 * Copyright 2002-2014 the original author or authors.
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

package org.springframework.context.support;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * {@link AbstractRefreshableApplicationContext} subclass that adds common handling
 * of specified config locations. Serves as base class for XML-based application
 * context implementations such as {@link ClassPathXmlApplicationContext} and
 * {@link FileSystemXmlApplicationContext}, as well as
 * {@link org.springframework.web.context.support.XmlWebApplicationContext} and
 * {@link org.springframework.web.portlet.context.XmlPortletApplicationContext}.
 *
 * @author Juergen Hoeller
 * @since 2.5.2
 * @see #setConfigLocation
 * @see #setConfigLocations
 * @see #getDefaultConfigLocations
 */
public abstract class AbstractRefreshableConfigApplicationContext extends AbstractRefreshableApplicationContext
		implements BeanNameAware, InitializingBean {

	/**
	 * configLocations bean����·�����顾ʹ�ò�ͬbean��ȡ�����ؽ���ע�ᵽ�ڲ�����bean����DefaultListableBeanFactory��
	 */
	private String[] configLocations;

	/**
	 * �Ƿ������ⲿ����application ID
	 */
	private boolean setIdCalled = false;


	/**
	 * ����һ��û�и�������AbstractRefreshableConfigApplicationContext��
	 */
	public AbstractRefreshableConfigApplicationContext() {
	}

	/**
	 * ʹ�ø����ĸ������Ĵ����µ�AbstractRefreshableConfigApplicationContext��
	 * @param parent the parent context
	 */
	public AbstractRefreshableConfigApplicationContext(ApplicationContext parent) {
		super(parent);
	}


	/**
	 * ����location�ö��ţ��ֺŻ�ո�ָ��ɶ��bean����·������
	 */
	public void setConfigLocation(String location) {
		setConfigLocations(StringUtils.tokenizeToStringArray(location, CONFIG_LOCATION_DELIMITERS));
	}

	/**
	 * �����ⲿ����bean����·�����飬ʹ��{@link #getDefaultConfigLocations}ȥ����ÿһ��configLocation
	 */
	public void setConfigLocations(String... locations) {
		if (locations != null) {
			Assert.noNullElements(locations, "Config locations must not be null");
			this.configLocations = new String[locations.length];
			for (int i = 0; i < locations.length; i++) {
				this.configLocations[i] = resolvePath(locations[i]).trim();
			}
		}
		else {
			this.configLocations = null;
		}
	}

	/**
	 * ����bean����·������
	 * ÿһ����Դ·������ʹ�� *ģʽ����Щģʽ��ͨ��ResourcePatternResolver������
	 * <p>Ĭ��ʵ�ַ���{@code null}��, ����ʵ�ֿ��Ը�����Ҫȥʵ��{@link #getDefaultConfigLocations}����Ĭ����Դ·������
	 * @return an array of resource locations, or {@code null} if none
	 * @see #getResources
	 * @see #getResourcePatternResolver
	 */
	protected String[] getConfigLocations() {
		return (this.configLocations != null ? this.configLocations : getDefaultConfigLocations());
	}

	/**
	 * ��Ҫ����ʵ�ֿ��Ը�����Ҫȥʵ�ַ���Ĭ��bean����·������
	 * @return an array of default config locations, if any
	 * @see #setConfigLocations
	 */
	protected String[] getDefaultConfigLocations() {
		return null;
	}

	/**
	 * ��������·�������б�Ҫ������ʹ��environment����ֵ�滻ռλ����
	 * �ο�{@link #PropertySourcesPropertyResolverTests.resolveRequiredPlaceholders}
	 * @param path the original file path
	 * @return the resolved file path
	 * @see org.springframework.core.env.Environment#resolveRequiredPlaceholders(String)
	 */
	protected String resolvePath(String path) {
		return getEnvironment().resolveRequiredPlaceholders(path);
	}


	@Override
	public void setId(String id) {
		super.setId(id);
		this.setIdCalled = true;
	}


	@Override
	public void setBeanName(String name) {
		if (!this.setIdCalled) {
			super.setId(name);
			setDisplayName("ApplicationContext '" + name + "'");
		}
	}

	/**
	 * ���û���ھ���contextû�д��򴥷�{@link #refresh����}��
	 */
	@Override
	public void afterPropertiesSet() {
		if (!isActive()) {
			refresh();
		}
	}

}
