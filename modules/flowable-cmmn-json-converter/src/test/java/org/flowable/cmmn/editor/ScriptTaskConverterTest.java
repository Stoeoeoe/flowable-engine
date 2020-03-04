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
package org.flowable.cmmn.editor;

import static org.assertj.core.api.Assertions.assertThat;

import org.flowable.cmmn.model.CmmnModel;
import org.flowable.cmmn.model.PlanItem;
import org.flowable.cmmn.model.PlanItemControl;
import org.flowable.cmmn.model.Stage;

/**
 * @author Joram Barrez
 */
public class ScriptTaskConverterTest extends AbstractConverterTest {

    @Override
    protected String getResource() {
        return "test.scriptTaskModel.json";
    }

    @Override
    protected void validateModel(CmmnModel cmmnModel) {
        Stage planModel = cmmnModel.getPrimaryCase().getPlanModel();
        assertThat(planModel.getPlanItemDefinitionMap()).hasSize(2);

        PlanItem planItemA = planModel.getPlanItems().stream().filter(p -> p.getName().equals("A")).findFirst().get();
        assertThat(planItemA.getName()).isEqualTo("A");
        assertThat(planItemA.getItemControl()).isNull();

        PlanItem planItemB = planModel.getPlanItems().stream().filter(p -> p.getName().equals("B")).findFirst().get();
        assertThat(planItemB.getName()).isEqualTo("B");
        PlanItemControl planItemControlB = planItemB.getItemControl();
        assertThat(planItemControlB.getRequiredRule()).isNotNull();
        assertThat(planItemControlB.getRepetitionRule()).isNotNull();
        assertThat(planItemControlB.getManualActivationRule()).isNotNull();
    }

}