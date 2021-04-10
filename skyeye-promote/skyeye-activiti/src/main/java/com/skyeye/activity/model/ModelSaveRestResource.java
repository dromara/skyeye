/**
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved.
 */
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

package com.skyeye.activity.model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import org.activiti.editor.constants.ModelDataJsonConstants;
import org.activiti.engine.ActivitiException;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Model;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * @ClassName: ModelSaveRestResource
 * @Description: 编辑器制图之后，将节点信息以json的形式提交给这个Controller，然后由其进行持久化操作
 * @author: skyeye云系列--卫志强
 * @date: 2021/4/10 22:03
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@RestController
@RequestMapping(value = "/service")
public class ModelSaveRestResource implements ModelDataJsonConstants {

	protected static final Logger LOGGER = LoggerFactory.getLogger(ModelSaveRestResource.class);

	@Autowired
	private RepositoryService repositoryService;

	@Autowired
	private ObjectMapper objectMapper;

	@RequestMapping(value = "/model/{modelId}/save", method = RequestMethod.PUT)
	@ResponseStatus(value = HttpStatus.OK)
	public void saveModel(@PathVariable String modelId, @RequestParam("name") String name,
			@RequestParam("json_xml") String json_xml, @RequestParam("svg_xml") String svg_xml,
			@RequestParam("description") String description) {
		try {

			Model model = repositoryService.getModel(modelId);

			ObjectNode modelJson = (ObjectNode) objectMapper.readTree(model.getMetaInfo());

			modelJson.put(MODEL_NAME, name);
			modelJson.put(MODEL_DESCRIPTION, description);
			model.setMetaInfo(modelJson.toString());
			model.setName(name);

			repositoryService.saveModel(model);

			repositoryService.addModelEditorSource(model.getId(), json_xml.getBytes("utf-8"));

			InputStream svgStream = new ByteArrayInputStream(svg_xml.getBytes("utf-8"));
			TranscoderInput input = new TranscoderInput(svgStream);

			PNGTranscoder transcoder = new PNGTranscoder();
			// Setup output
			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			TranscoderOutput output = new TranscoderOutput(outStream);

			// Do the transformation
			transcoder.transcode(input, output);
			final byte[] result = outStream.toByteArray();
			repositoryService.addModelEditorSourceExtra(model.getId(), result);
			outStream.close();

		} catch (Exception e) {
			LOGGER.error("Error saving model", e);
			throw new ActivitiException("Error saving model", e);
		}
	}
}
