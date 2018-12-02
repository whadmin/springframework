/*
 * Copyright 2002-2018 the original author or authors.
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

package org.springframework.context.event;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.core.ResolvableType;

/**
 * ��׼{@link ApplicationListener}�ӿڵ���չ���壬�ṩ��������ѡ��֧�ֵ��¼���Դ���͵�ѡ��
 *
 * <p>��Spring Framework 4.2��ʼ���˽ӿ�ȡ���˻������* {@link SmartApplicationListener}������ȫ������ͨ���¼����͡�
 *
 * @author Stephane Nicoll
 * @since 4.2
 * @see SmartApplicationListener
 * @see GenericApplicationListenerAdapter
 */
public interface GenericApplicationListener extends ApplicationListener<ApplicationEvent>, Ordered {

	/**
	 * ȷ�����������Ƿ�ʵ��֧�ָ������¼����͡�
	 * @param eventType the event type (never {@code null})
	 */
	boolean supportsEventType(ResolvableType eventType);

	/**
	 * ȷ�����������Ƿ�ʵ��֧�ָ�����Դ���͡�
	 * @param sourceType the source type, or {@code null} if no source
	 */
	boolean supportsSourceType(Class<?> sourceType);

}
