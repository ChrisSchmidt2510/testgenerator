package de.nvg.valuetracker.blueprint;

import java.util.List;

public interface BluePrint {

	public List<BluePrint> getPreExecuteBluePrints();

	public Object getReference();

	public String getName();

	public boolean isComplexType();

	public boolean isBuild();

}
