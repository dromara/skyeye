{{#bean}}
	<div class="layui-collapse">
        <div class="layui-colla-item">
            <h2 class="layui-colla-title"></h2>
            <div class="layui-colla-content">

            </div>
        </div>
    </div>
	<div class="layui-form-item layui-col-xs12">
		<span class="hr-title">基本信息</span><hr>
	</div>
    <div class="layui-form-item">
        <label class="layui-form-label">模板后缀<i class="red">*</i></label>
        <div class="layui-input-block">
        	<input type="text" id="modelName" name="modelName" win-verify="required" placeholder="请输入模板后缀" class="layui-input" maxlength="20" value="{{modelName}}"/>
        	<div class="layui-form-mid layui-word-aux">例如：想要生成BossPersonRequireDao.java文件，只需要填写 Dao 即可</div>
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">模板类型<i class="red">*</i></label>
        <div class="layui-input-block">
        	<select id="modelType" name="modelType" class="layui-input" win-verify="required" lay-filter="selectParent">
        		<option value=""></option>
        		<optgroup label="CLIKE">
                    <option value="Java" selected="selected">Java</option>
                    <option value="C/C++">C++</option>
                    <option value="Objective-C">Objective-C</option>
                    <option value="Scala">Scala</option>
                    <option value="Kotlin">Kotlin</option>
                    <option value="Ceylon">Ceylon</option>
        		</optgroup>
        		<optgroup label="XML">
                    <option value="xml">xml</option>
                    <option value="html">html</option>
        		</optgroup>
        		<optgroup label="CSS">
                    <option value="css">css</option>
        		</optgroup>
        		<optgroup label="HTMLMIXED">
                    <option value="htmlmixed">htmlmixed</option>
                    <option value="htmlhh">html混合模式</option>
        		</optgroup>
        		<optgroup label="JAVASCRIPT">
                    <option value="javascript">javascript</option>
        		</optgroup>
        		<optgroup label="NGINX">
                    <option value="nginx">nginx</option>
        		</optgroup>
        		<optgroup label="SOLR">
                    <option value="solr">solr</option>
        		</optgroup>
        		<optgroup label="SQL">
                    <option value="sql">sql</option>
        		</optgroup>
        		<optgroup label="VUE">
                    <option value="vue">vue</option>
        		</optgroup>
            </select>
        </div>
    </div>
    <div class="layui-form-item">
        <label class="layui-form-label">模板内容<i class="red">*</i></label>
        <div class="layui-input-block">
        	<textarea id="modelContent">{{modelContent}}</textarea>
        </div>
    </div>
    <div class="layui-form-item">
        <div class="layui-input-block">
            <button class="winui-btn" id="cancle"><language showName="com.skyeye.cancel"></language></button>
            <button class="winui-btn" lay-submit lay-filter="formEditBean"><language showName="com.skyeye.save"></language></button>
        </div>
    </div>
{{/bean}}