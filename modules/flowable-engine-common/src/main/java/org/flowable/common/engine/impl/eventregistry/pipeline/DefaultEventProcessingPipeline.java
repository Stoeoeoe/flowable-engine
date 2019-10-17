/* Licensed under the Apache License, Version 2.0 (the "License");
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
package org.flowable.common.engine.impl.eventregistry.pipeline;

import java.util.Collection;

import org.flowable.common.engine.api.eventbus.FlowableEventBusEvent;
import org.flowable.common.engine.api.eventregistry.EventRegistry;
import org.flowable.common.engine.api.eventregistry.InboundEventDeserializer;
import org.flowable.common.engine.api.eventregistry.InboundEventKeyDetector;
import org.flowable.common.engine.api.eventregistry.InboundEventPayloadExtractor;
import org.flowable.common.engine.api.eventregistry.InboundEventProcessingPipeline;
import org.flowable.common.engine.api.eventregistry.InboundEventTransformer;
import org.flowable.common.engine.api.eventregistry.definition.EventDefinition;
import org.flowable.common.engine.api.eventregistry.runtime.EventCorrelationParameterInstance;
import org.flowable.common.engine.api.eventregistry.runtime.EventInstance;
import org.flowable.common.engine.api.eventregistry.runtime.EventPayloadInstance;
import org.flowable.common.engine.impl.eventregistry.runtime.EventInstanceImpl;

/**
 * @author Joram Barrez
 * @author Filip Hrisafov
 */
public class DefaultEventProcessingPipeline<T> implements InboundEventProcessingPipeline {

    protected EventRegistry eventRegistry;
    protected InboundEventDeserializer<T> inboundEventDeserializer;
    protected InboundEventKeyDetector<T> inboundEventKeyDetector;
    protected InboundEventPayloadExtractor<T> inboundEventPayloadExtractor;
    protected InboundEventTransformer inboundEventTransformer;

    public DefaultEventProcessingPipeline(EventRegistry eventRegistry,
            InboundEventDeserializer<T> inboundEventDeserializer,
            InboundEventKeyDetector<T> inboundEventKeyDetector,
            InboundEventPayloadExtractor<T> inboundEventPayloadExtractor,
            InboundEventTransformer inboundEventTransformer) {
        this.eventRegistry = eventRegistry;
        this.inboundEventDeserializer = inboundEventDeserializer;
        this.inboundEventKeyDetector = inboundEventKeyDetector;
        this.inboundEventPayloadExtractor = inboundEventPayloadExtractor;
        this.inboundEventTransformer = inboundEventTransformer;
    }

    @Override
    public Collection<FlowableEventBusEvent> run(String channelKey, String rawEvent) {
        T event = deserialize(rawEvent);
        String eventKey = detectEventDefinitionKey(event);

        EventDefinition eventDefinition = eventRegistry.getEventDefinition(eventKey);

        EventInstanceImpl eventInstance = new EventInstanceImpl(
            eventDefinition,
            extractCorrelationParameters(eventDefinition, event),
            extractPayload(eventDefinition, event)
        );

        // TODO: change transform() to EventInstance instead of eventBusEvent
        return transform(eventInstance);
    }

    public T deserialize(String rawEvent) {
        return inboundEventDeserializer.deserialize(rawEvent);
    }

    public String detectEventDefinitionKey(T event) {
        return inboundEventKeyDetector.detectEventDefinitionKey(event);
    }

    public Collection<EventCorrelationParameterInstance> extractCorrelationParameters(EventDefinition eventDefinition, T event) {
        return inboundEventPayloadExtractor.extractCorrelationParameters(eventDefinition, event);
    }

    public Collection<EventPayloadInstance> extractPayload(EventDefinition eventDefinition, T event) {
        return inboundEventPayloadExtractor.extractPayload(eventDefinition, event);
    }

    public Collection<FlowableEventBusEvent> transform(EventInstance eventInstance) {
        return inboundEventTransformer.transform(eventInstance);
    }
}
