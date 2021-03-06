/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.echarts.web.other;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;

import com.github.abel533.echarts.Option;
import com.github.abel533.echarts.code.Magic;
import com.github.abel533.echarts.code.Tool;
import com.github.abel533.echarts.code.Trigger;
import com.github.abel533.echarts.code.X;
import com.github.abel533.echarts.data.LineData;
import com.github.abel533.echarts.data.PieData;
import com.github.abel533.echarts.feature.MagicType;
import com.github.abel533.echarts.json.GsonOption;
import com.github.abel533.echarts.series.Funnel;
import com.github.abel533.echarts.series.Pie;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.collect.Lists;
import com.jeeplus.common.utils.DateUtils;
import com.jeeplus.common.config.Global;
import com.jeeplus.common.json.AjaxJson;
import com.jeeplus.core.persistence.Page;
import com.jeeplus.core.web.BaseController;
import com.jeeplus.common.utils.StringUtils;
import com.jeeplus.common.utils.excel.ExportExcel;
import com.jeeplus.common.utils.excel.ImportExcel;
import com.jeeplus.modules.echarts.entity.other.TestPieClass;
import com.jeeplus.modules.echarts.service.other.TestPieClassService;

/**
 * ??????Controller
 * @author lgf
 * @version 2017-06-04
 */
@Controller
@RequestMapping(value = "${adminPath}/echarts/other/testPieClass")
public class TestPieClassController extends BaseController {

	@Autowired
	private TestPieClassService testPieClassService;
	
	@ModelAttribute
	public TestPieClass get(@RequestParam(required=false) String id) {
		TestPieClass entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = testPieClassService.get(id);
		}
		if (entity == null){
			entity = new TestPieClass();
		}
		return entity;
	}

	@ResponseBody
	@RequestMapping("option")
	public GsonOption getOption(){
		GsonOption option = new GsonOption();
		//timeline???????????????????????????Option
		option.title().text("???????????????").subtext("????????????");
		option.toolbox().show(true).feature(Tool.mark, Tool.dataView, Tool.restore, Tool.saveAsImage, new MagicType(Magic.pie, Magic.funnel)
				.option(new MagicType.Option().funnel(
						new Funnel().x("25%").width("50%").funnelAlign(X.left).max(1548))));

		//??????11?????????
		List<TestPieClass> list = testPieClassService.findList(new TestPieClass());
		ArrayList arrayList = new ArrayList();
		for(TestPieClass p:list){
			arrayList.add(new PieData(p.getClassName(), p.getNum()));
		}
		Pie pie = new Pie().name("???????????????");
		pie.setData(arrayList);
		option.series(pie);;
		return option;
	}
	/**
	 * ??????????????????
	 */
	@RequiresPermissions("echarts:other:testPieClass:list")
	@RequestMapping(value = {"list", ""})
	public String list() {
		return "modules/echarts/other/testPieClassList";
	}
	
		/**
	 * ??????????????????
	 */
	@ResponseBody
	@RequiresPermissions("echarts:other:testPieClass:list")
	@RequestMapping(value = "data")
	public Map<String, Object> data(TestPieClass testPieClass, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<TestPieClass> page = testPieClassService.findPage(new Page<TestPieClass>(request, response), testPieClass); 
		return getBootstrapData(page);
	}

	/**
	 * ??????????????????????????????????????????
	 */
	@RequiresPermissions(value={"echarts:other:testPieClass:view","echarts:other:testPieClass:add","echarts:other:testPieClass:edit"},logical=Logical.OR)
	@RequestMapping(value = "form")
	public String form(TestPieClass testPieClass, Model model) {
		model.addAttribute("testPieClass", testPieClass);
		return "modules/echarts/other/testPieClassForm";
	}

	/**
	 * ????????????
	 */
	@ResponseBody
	@RequiresPermissions(value={"echarts:other:testPieClass:add","echarts:other:testPieClass:edit"},logical=Logical.OR)
	@RequestMapping(value = "save")
	public AjaxJson save(TestPieClass testPieClass, Model model) throws Exception{
		AjaxJson j = new AjaxJson();
		/**
		 * ??????hibernate-validation????????????
		 */
		String errMsg = beanValidator(testPieClass);
		if (StringUtils.isNotBlank(errMsg)){
			j.setSuccess(false);
			j.setMsg(errMsg);
			return j;
		}
		testPieClassService.save(testPieClass);//????????????????????????
		j.setSuccess(true);
		j.setMsg("??????????????????");
		return j;
	}
	
	/**
	 * ????????????
	 */
	@ResponseBody
	@RequiresPermissions("echarts:other:testPieClass:del")
	@RequestMapping(value = "delete")
	public AjaxJson delete(TestPieClass testPieClass) {
		AjaxJson j = new AjaxJson();
		testPieClassService.delete(testPieClass);
		j.setMsg("??????????????????");
		return j;
	}
	
	/**
	 * ??????????????????
	 */
	@ResponseBody
	@RequiresPermissions("echarts:other:testPieClass:del")
	@RequestMapping(value = "deleteAll")
	public AjaxJson deleteAll(String ids) {
		AjaxJson j = new AjaxJson();
		String idArray[] =ids.split(",");
		for(String id : idArray){
			testPieClassService.delete(testPieClassService.get(id));
		}
		j.setMsg("??????????????????");
		return j;
	}
	
	/**
	 * ??????excel??????
	 */
	@ResponseBody
	@RequiresPermissions("echarts:other:testPieClass:export")
    @RequestMapping(value = "export", method=RequestMethod.POST)
    public AjaxJson exportFile(TestPieClass testPieClass, HttpServletRequest request, HttpServletResponse response) {
		AjaxJson j = new AjaxJson();
		try {
            String fileName = "??????"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            Page<TestPieClass> page = testPieClassService.findPage(new Page<TestPieClass>(request, response, -1), testPieClass);
    		new ExportExcel("??????", TestPieClass.class).setDataList(page.getList()).write(response, fileName).dispose();
    		j.setSuccess(true);
    		j.setMsg("???????????????");
    		return j;
		} catch (Exception e) {
			j.setSuccess(false);
			j.setMsg("??????????????????????????????????????????"+e.getMessage());
		}
			return j;
    }

	/**
	 * ??????Excel??????

	 */
	@RequiresPermissions("echarts:other:testPieClass:import")
    @RequestMapping(value = "import", method=RequestMethod.POST)
    public String importFile(MultipartFile file, RedirectAttributes redirectAttributes) {
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<TestPieClass> list = ei.getDataList(TestPieClass.class);
			for (TestPieClass testPieClass : list){
				try{
					testPieClassService.save(testPieClass);
					successNum++;
				}catch(ConstraintViolationException ex){
					failureNum++;
				}catch (Exception ex) {
					failureNum++;
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "????????? "+failureNum+" ??????????????????");
			}
			addMessage(redirectAttributes, "??????????????? "+successNum+" ???????????????"+failureMsg);
		} catch (Exception e) {
			addMessage(redirectAttributes, "????????????????????????????????????"+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/echarts/other/testPieClass/?repage";
    }
	
	/**
	 * ??????????????????????????????
	 */
	@RequiresPermissions("echarts:other:testPieClass:import")
    @RequestMapping(value = "import/template")
    public String importFileTemplate(HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
            String fileName = "????????????????????????.xlsx";
    		List<TestPieClass> list = Lists.newArrayList(); 
    		new ExportExcel("????????????", TestPieClass.class, 1).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			addMessage(redirectAttributes, "??????????????????????????????????????????"+e.getMessage());
		}
		return "redirect:"+Global.getAdminPath()+"/echarts/other/testPieClass/?repage";
    }

}