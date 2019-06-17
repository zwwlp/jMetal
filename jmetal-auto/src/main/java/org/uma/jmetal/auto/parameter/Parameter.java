package org.uma.jmetal.auto.parameter;

import org.uma.jmetal.auto.algorithm.nsgaiib.MissingParameterException;

import java.util.*;
import java.util.function.Function;

/**
 * Abstract class representing generic parameters. Any parameter has a name and a value that is
 * assigned by parsing an array called {@link Parameter#args} which contains sequences of pairs
 * [key, value]. For example, the args field could contain these values:
 * ["--populationSize", "100", "--offspringPopulationSize", "100", "--createInitialSolutions", "random"]
 *
 * @author Antonio J. Nebro
 * @param <T> Type of the parameter
 */
public abstract class Parameter<T> {
  private T value;
  private String name;
  private String[] args;
  private Map<String, Parameter<?>> specificParameters = new HashMap<>();
  private List<Parameter<?>> globalParameters = new ArrayList<>();

  public Parameter(String name, String[] args) {
    this.name = name;
    this.args = args;
  }

  public T on(String key, String[] args, Function<String, T> parser) {
    return parser.apply(retrieve(args, key));
  }

  private String retrieve(String[] args, String key) {
    int index = Arrays.asList(args).indexOf(key);
    if (index == -1 || index == args.length - 1) {
      throw new MissingParameterException(key);
    } else {
      return args[index + 1];
    }
  }

  public abstract void check();

  public abstract Parameter<T> parse();

  public String getName() {
    return name;
  }

  public Map<String, Parameter<?>> getSpecificParameters() {
    return specificParameters;
  }

  public void addSpecificParameter(String dependsOn, Parameter<?> parameter) {
    specificParameters.put(dependsOn, parameter);
  }

  public List<Parameter<?>> getGlobalParameters() {
    return globalParameters;
  }

  public void addGlobalParameter(Parameter<?> parameter) {
    globalParameters.add(parameter);
  }

  public T getValue() {
    return value;
  }

  public void setValue(T value) {
    this.value = value;
  }

  public String[] getArgs() {
    return args;
  }

  protected Parameter<?> findGlobalParameter(String parameterName) {
    Parameter<?> result =
        getGlobalParameters().stream()
            .filter(parameter -> parameter.getName().equals(parameterName))
            .findFirst()
            .orElse(null);

    return result;
  }

  protected Parameter<?> findSpecificParameter(String parameterName) {
    Parameter<?> result =
        getSpecificParameters().entrySet().stream()
            .filter(pair -> pair.getValue().getName().equals(parameterName))
            .findFirst()
            .orElse(null)
            .getValue();

    return result;
  }

  @Override
  public String toString() {
    String result = "Name: " + getName() + ": " + "Value: " + getValue();
    if (globalParameters.size() > 0) {
      result += "\n\t";
      for (Parameter<?> parameter : globalParameters) {
        result += " \n -> " + parameter.toString();
      }
    }
    if (specificParameters.size() > 0) {
      result += "\n\t";

      for (Parameter<?> parameter : specificParameters.values()) {
        result += " \n -> " + parameter.toString();
      }
    }
    return result;
  }
}
