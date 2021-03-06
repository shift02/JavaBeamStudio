package net.orekyuu.javatter.core.command;

import net.orekyuu.javatter.api.command.Command;
import net.orekyuu.javatter.api.service.CurrentTweetAreaService;
import net.orekyuu.javatter.api.service.EnvironmentService;
import net.orekyuu.javatter.api.util.lookup.Lookup;

import java.util.List;

public class VersionCommand implements Command {
    @Override
    public String command() {
        return "version";
    }

    @Override
    public String help() {
        return "現在のJavaビーム工房のバージョンを調べます";
    }

    @Override
    public void exec(List<String> args) {
        //TODO 実行時にバージョンチェックを行う
        CurrentTweetAreaService service = Lookup.lookup(CurrentTweetAreaService.class);
        EnvironmentService environmentService = Lookup.lookup(EnvironmentService.class);
        service.setText("Javaビーム工房 " + environmentService.getJavaBeamStudioVersion()
                + "\nAPIバージョン " + environmentService.getJavaBeamStudioApiVersion());
    }
}
