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
	 * 返回在解析属性值为指定类型的 类型转化器{@link ConfigurableConversionService}。 ,
	 * @see PropertyResolver#getProperty(String, Class)
	 * @see org.springframework.core.convert.converter.ConverterRegistry#addConverter
	 */
	ConfigurableConversionService getConversionService();

	/**
	 * 设置在解析属性值为指定类型的 类型转化器{@link ConfigurableConversionService}。 ,
	 * @see PropertyResolver#getProperty(String, Class)
	 * @see #getConversionService()
	 * @see org.springframework.core.convert.converter.ConverterRegistry#addConverter
	 */
	void setConversionService(ConfigurableConversionService conversionService);

	/**
	 * 设置此解析程序替换的占位符必须以此开头的前缀。
	 */
	void setPlaceholderPrefix(String placeholderPrefix);

	/**
	 * 设置由此解析程序替换的占位符必须以此结尾的后缀。
	 */
	void setPlaceholderSuffix(String placeholderSuffix);

	/**
	 * 指定由此
	 * 解析程序替换的占位符之间的分隔字符及其关联的默认值，如果不应将此
	 * 特殊字符作为值分隔符处理，则指定{@code null}。
	 */
	void setValueSeparator(String valueSeparator);

	/**
	 * 设置当遇到嵌套在给定属性值内的不可解析的占位符*时是否抛出异常。 ,
	 * {@code false}值表示出现无法解析占位符即将抛出异常。 ,
	 * {@code true}值表示出现无法解析占位符以未解析的*$ {...}形式传递。 ,
	 * key1:Hello {key2}
	 * key2:world
	 * getProperty(String key1)== Hello world
	 */
	void setIgnoreUnresolvableNestedPlaceholders(boolean ignoreUnresolvableNestedPlaceholders);

	/**
	 * 指定必须存在哪些属性，以进行验证
	 * {@link #validateRequiredProperties()}.
	 */
	void setRequiredProperties(String... requiredProperties);

	/**
	 * 验证* {@link #setRequiredProperties}指定的每个属性是否存在并解析为*非{@ code null}值。
	 * @throws MissingRequiredPropertiesException if any of the required
	 * properties are not resolvable.
	 */
	void validateRequiredProperties() throws MissingRequiredPropertiesException;

}
