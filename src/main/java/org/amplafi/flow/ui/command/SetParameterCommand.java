package org.amplafi.flow.ui.command;

import java.util.Map;
import java.util.regex.Matcher;

import org.amplafi.flow.utils.AdminTool;

public class SetParameterCommand extends AbstractShellCommand {

    public SetParameterCommand(String setOptions) {
        super(setOptions);
    }

    @Override
    public void execute(AdminTool adminTool) {
        String options = this.getOptions();
        Matcher m = ONE_AND_MAYBE_TWO_WORDS.matcher(options);
        if(m.matches()){
            String parameterName = m.group(1);
            String parameterValue = m.group(2);
            adminTool.getBindingFactory().setParameter(parameterName, parameterValue);
        } else if ( NOTHING.matcher(options).find() ) {
            Map<?,?> defaultParameters = adminTool.getBindingFactory().getDefaultParameters();
            for(Map.Entry<?,?> entry: defaultParameters.entrySet()) {
                System.out.print(entry.getKey());
                System.out.print(" = ");
                System.out.println(entry.getValue());
            }
        }
    }

}
