package de.nvg.valuetracker.storage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import de.nvg.valuetracker.blueprint.BluePrint;

public class ValueStorage
{
  private static final ValueStorage INSTANCE = new ValueStorage();

  private List<BluePrint> bluePrints = new ArrayList<>();

  private Map<Class<?>, List<BluePrint>> bluePrintsPerClass = new HashMap<>();

  private ValueStorage()
  {}

  public static ValueStorage getInstance()
  {
    return INSTANCE;
  }

  public void addBluePrint(BluePrint bluePrint)
  {
    bluePrints.add(bluePrint);
  }

  public void addBluePrintPerClass(Class<?> clazz, BluePrint bluePrint)
  {

    if (bluePrintsPerClass.containsKey(clazz))
    {
      bluePrintsPerClass.get(clazz).add(bluePrint);
    }
    else
    {
      bluePrintsPerClass.put(clazz, new ArrayList<>(Arrays.asList(bluePrint)));
    }
  }

  public BluePrint getBluePrintForReference(Object reference)
  {
    List<BluePrint> bluePrintsForClass = bluePrintsPerClass.get(reference.getClass());

    if (bluePrintsForClass != null)
    {

      for (BluePrint bluePrint : bluePrintsForClass)
      {
        if (bluePrint.getReference() == reference)
        {
          return bluePrint;
        }
      }
    }

    return null;
  }

  public Collection<BluePrint> getBluePrints()
  {
    return Collections.unmodifiableCollection(bluePrints);
  }
}
