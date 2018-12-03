/*
 * Copyright 2002-2016 the original author or authors.
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

package org.springframework.core.env;

import org.springframework.core.convert.support.ConfigurableConversionService;

/**
 * Configuration interface to be implemented by most if not all {@link PropertyResolver}
 * types. Provides facilities for accessing and customizing the
 * {@link org.springframework.core.convert.ConversionService ConversionService}
 * used when converting property values from one type to another.
 *
 * @author Chris Beams
 * @since 3.1
 */
public interface ConfigurablePropertyResolver extends PropertyResolver {

	/**
	 * �����ڽ�������ֵΪָ�����͵� ����ת����{@link ConfigurableConversionService}�� ,
	 * @see PropertyResolver#getProperty(String, Class)
	 * @see org.springframework.core.convert.converter.ConverterRegistry#addConverter
	 */
	ConfigurableConversionService getConversionService();

	/**
	 * �����ڽ�������ֵΪָ�����͵� ����ת����{@link ConfigurableConversionService}�� ,
	 * @see PropertyResolver#getProperty(String, Class)
	 * @see #getConversionService()
	 * @see org.springframework.core.convert.converter.ConverterRegistry#addConverter
	 */
	void setConversionService(ConfigurableConversionService conversionService);

	/**
	 * ���ô˽��������滻��ռλ�������Դ˿�ͷ��ǰ׺��
	 */
	void setPlaceholderPrefix(String placeholderPrefix);

	/**
	 * �����ɴ˽��������滻��ռλ�������Դ˽�β�ĺ�׺��
	 */
	void setPlaceholderSuffix(String placeholderSuffix);

	/**
	 * ָ���ɴ�
	 * ���������滻��ռλ��֮��ķָ��ַ����������Ĭ��ֵ�������Ӧ����
	 * �����ַ���Ϊֵ�ָ���������ָ��{@code null}��
	 */
	void setValueSeparator(String valueSeparator);

	/**
	 * ���õ�����Ƕ���ڸ�������ֵ�ڵĲ��ɽ�����ռλ��*ʱ�Ƿ��׳��쳣�� ,
	 * {@code false}ֵ��ʾ�����޷�����ռλ�������׳��쳣�� ,
	 * {@code true}ֵ��ʾ�����޷�����ռλ����δ������*$ {...}��ʽ���ݡ� ,
	 * key1:Hello {key2}
	 * key2:world
	 * getProperty(String key1)== Hello world
	 */
	void setIgnoreUnresolvableNestedPlaceholders(boolean ignoreUnresolvableNestedPlaceholders);

	/**
	 * ָ�����������Щ���ԣ��Խ�����֤
	 * {@link #validateRequiredProperties()}.
	 */
	void setRequiredProperties(String... requiredProperties);

	/**
	 * ��֤* {@link #setRequiredProperties}ָ����ÿ�������Ƿ���ڲ�����Ϊ*��{@ code null}ֵ��
	 * @throws MissingRequiredPropertiesException if any of the required
	 * properties are not resolvable.
	 */
	void validateRequiredProperties() throws MissingRequiredPropertiesException;

}
