#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )

package ${package}.pe.sink.${packageName};

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.wrapper.context.EventSinkRuntimeContext;
import org.apache.streampipes.wrapper.runtime.EventSink;

import org.slf4j.Logger;


public class ${classNamePrefix} implements EventSink<${classNamePrefix}Parameters> {

private static Logger LOG;

  @Override
  public void onInvocation(${classNamePrefix}Parameters parameters, EventSinkRuntimeContext runtimeContext) throws SpRuntimeException {

  }

  @Override
  public void onEvent(Event event) throws SpRuntimeException {

  }

  @Override
  public void onDetach() throws SpRuntimeException {

  }
}
