package com.indrasol.essbase.essbasesheetplugin.util;

import java.util.ArrayList;
import java.util.List;

import com.essbase.api.base.EssException;
import com.essbase.api.base.IEssBaseObject;
import com.essbase.api.metadata.IEssCubeOutline;
import com.essbase.api.metadata.IEssDimension;
import com.essbase.api.metadata.IEssMember;

import org.springframework.stereotype.Component;

@Component
public class EssbaseUtil {

	public static List<IEssDimension> getDimensionsAsList(IEssCubeOutline cubeOutLine) throws EssException {
		List<IEssDimension> dimenionsList = new ArrayList<IEssDimension>();
		IEssBaseObject[] dimensions = cubeOutLine.getDimensions().getAll();
		for (int i = 0; i < dimensions.length; i++) {
			IEssDimension dimension = (IEssDimension) dimensions[i];
			dimenionsList.add(dimension);
			System.out.println("Dimensions:" + dimension.getName());
		}
		return dimenionsList;
	}

	public static List<IEssMember> getMembers(IEssDimension dimension) throws EssException {
		List<IEssMember> membersList = new ArrayList<IEssMember>();
		IEssBaseObject[] members = dimension.getDimensionRootMember().getChildMembers().getAll();
		for (int i = 0; i < members.length; i++) {
			IEssMember member = (IEssMember) members[i];
			membersList.add(member);
			System.out.println("member:" + member.getName());
		}
		return membersList;
	}

	public static List<IEssMember> getSubMembers(IEssMember member) throws EssException {
		List<IEssMember> membersList = new ArrayList<IEssMember>();
		IEssBaseObject[] members = member.getChildMembers().getAll();
		for (int i = 0; i < members.length; i++) {
			IEssMember subMember = (IEssMember) members[i];
			membersList.add(subMember);
			System.out.println("Submember:" + subMember.getName());
		}
		return membersList;
    }
    


}
