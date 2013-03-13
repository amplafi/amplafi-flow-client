<map version="freeplane 1.2.0">
<!--To view this file, download free mind mapping software Freeplane from http://freeplane.sourceforge.net -->
<node TEXT="Plan" ID="ID_448295637" CREATED="1326356734318" MODIFIED="1361329328769"><hook NAME="MapStyle">

<map_styles>
<stylenode LOCALIZED_TEXT="styles.root_node">
<stylenode LOCALIZED_TEXT="styles.predefined" POSITION="right">
<stylenode LOCALIZED_TEXT="default" MAX_WIDTH="600" COLOR="#000000" STYLE="as_parent">
<font NAME="SansSerif" SIZE="10" BOLD="false" ITALIC="false"/>
</stylenode>
<stylenode LOCALIZED_TEXT="defaultstyle.details"/>
<stylenode LOCALIZED_TEXT="defaultstyle.note"/>
<stylenode LOCALIZED_TEXT="defaultstyle.floating">
<edge STYLE="hide_edge"/>
<cloud COLOR="#f0f0f0" SHAPE="ROUND_RECT"/>
</stylenode>
</stylenode>
<stylenode LOCALIZED_TEXT="styles.user-defined" POSITION="right">
<stylenode LOCALIZED_TEXT="styles.topic" COLOR="#18898b" STYLE="fork">
<font NAME="Liberation Sans" SIZE="10" BOLD="true"/>
</stylenode>
<stylenode LOCALIZED_TEXT="styles.subtopic" COLOR="#cc3300" STYLE="fork">
<font NAME="Liberation Sans" SIZE="10" BOLD="true"/>
</stylenode>
<stylenode LOCALIZED_TEXT="styles.subsubtopic" COLOR="#669900">
<font NAME="Liberation Sans" SIZE="10" BOLD="true"/>
</stylenode>
<stylenode LOCALIZED_TEXT="styles.important">
<icon BUILTIN="yes"/>
</stylenode>
</stylenode>
<stylenode LOCALIZED_TEXT="styles.AutomaticLayout" POSITION="right">
<stylenode LOCALIZED_TEXT="AutomaticLayout.level.root" COLOR="#000000">
<font SIZE="18"/>
</stylenode>
<stylenode LOCALIZED_TEXT="AutomaticLayout.level,1" COLOR="#0033ff">
<font SIZE="16"/>
</stylenode>
<stylenode LOCALIZED_TEXT="AutomaticLayout.level,2" COLOR="#00b439">
<font SIZE="14"/>
</stylenode>
<stylenode LOCALIZED_TEXT="AutomaticLayout.level,3" COLOR="#990000">
<font SIZE="12"/>
</stylenode>
<stylenode LOCALIZED_TEXT="AutomaticLayout.level,4" COLOR="#111111">
<font SIZE="10"/>
</stylenode>
</stylenode>
</stylenode>
</map_styles>
</hook>
<node TEXT="DSL Improvements" POSITION="right" ID="ID_50910786" CREATED="1361329331847" MODIFIED="1361329367483">
<node TEXT="ParamValidation" ID="ID_1886287531" CREATED="1362102527768" MODIFIED="1362102533790">
<node TEXT="parameter specification in description line" ID="ID_1363999908" CREATED="1361330033118" MODIFIED="1361343382291" BACKGROUND_COLOR="#ff9c9c">
<node TEXT="param name" ID="ID_705651292" CREATED="1361330048206" MODIFIED="1361330054703"/>
<node TEXT="param description" ID="ID_1532211834" CREATED="1361330055790" MODIFIED="1361330359634"/>
<node TEXT="optional" ID="ID_1899538510" CREATED="1361330360247" MODIFIED="1361330369997"/>
<node TEXT="default value" ID="ID_125397965" CREATED="1361330370508" MODIFIED="1361330375575"/>
</node>
<node TEXT="automatic parameter validation and default value setup from parameter specification" ID="ID_1624639717" CREATED="1361330379595" MODIFIED="1361343382301" BACKGROUND_COLOR="#ff9c9c"/>
<node TEXT="automatic generation of parameter usage doc from description" ID="ID_1094814574" CREATED="1361330434930" MODIFIED="1361343382314" BACKGROUND_COLOR="#ff9c9c"/>
<node TEXT="Bugs" ID="ID_1652879890" CREATED="1362102544003" MODIFIED="1362102549225">
<node TEXT="Mandatory parameters should not have default values. Maybe call it an example value instead." ID="ID_780639421" CREATED="1362102550269" MODIFIED="1362102626268"/>
</node>
</node>
<node TEXT="add improved openPort method that behaves like request." ID="ID_670150402" CREATED="1362101938187" MODIFIED="1362101975420">
<node TEXT="e.g. requestAsync(&quot;FlowName&quot;,[&quot;param1&quot;:&quot;param2&quot;...etc]) returns ??? not sure would be nice if was JSON object but it might be an httpRequest object." ID="ID_991117320" CREATED="1362101980548" MODIFIED="1362102086292"/>
</node>
</node>
<node TEXT="Testing Server" POSITION="right" ID="ID_1282514577" CREATED="1361329378380" MODIFIED="1361329391908">
<node TEXT="Security Testing" ID="ID_1056914958" CREATED="1361329392973" MODIFIED="1361329417589"/>
<node TEXT="Regression Testing" ID="ID_1419235692" CREATED="1361329421437" MODIFIED="1361329435808">
<node TEXT="Guide: tests in git must pass unless there has been a change in behaviour from last release." ID="ID_901082121" CREATED="1361330216950" MODIFIED="1361330288280"/>
<node TEXT="Bugs should be logged and failing tests stored for analysis" ID="ID_497770515" CREATED="1361330254714" MODIFIED="1361330315904"/>
<node TEXT="steps" ID="ID_1118564093" CREATED="1361330014728" MODIFIED="1361330018678">
<node TEXT="Use logging proxy to record conversations with the server and generate tests" ID="ID_1151772956" CREATED="1361329854805" MODIFIED="1361329930942"/>
<node TEXT="set ignores for random field etc." ID="ID_1062067080" CREATED="1361329931811" MODIFIED="1361330009309"/>
<node TEXT="run against every version of the server" ID="ID_1237057254" CREATED="1361330062020" MODIFIED="1361330088169"/>
<node TEXT="report changes" ID="ID_1602892473" CREATED="1361330088731" MODIFIED="1361330092560"/>
<node TEXT="save failed tests in failed test folder by date." ID="ID_1124824657" CREATED="1361330093364" MODIFIED="1361330118917"/>
<node TEXT="report behaviour changes" ID="ID_470049372" CREATED="1361330119552" MODIFIED="1361330143953"/>
<node TEXT="record new tests that pass." ID="ID_1556795123" CREATED="1361330144748" MODIFIED="1361330169010"/>
<node TEXT="annotate failed tests with pat&apos;s comments." ID="ID_745038057" CREATED="1361330169626" MODIFIED="1361330202548"/>
</node>
<node TEXT="Question how to link a lest to a version of the server when we can&apos;t build from source." ID="ID_845979304" CREATED="1361330318677" MODIFIED="1361330352862"/>
</node>
<node TEXT="Obtain APIKey automatically????" ID="ID_405734552" CREATED="1361926557842" MODIFIED="1361926591537"/>
</node>
<node TEXT="Documentation" POSITION="right" ID="ID_54566777" CREATED="1361329702687" MODIFIED="1361329707255">
<node TEXT="How to set up poweredbypulse for groovy" ID="ID_1967255841" CREATED="1361329708019" MODIFIED="1361342828817" BACKGROUND_COLOR="#ff9c9c">
<node TEXT="We didn&apos;t manage to do this. I&apos;ll try again." ID="ID_1190353632" CREATED="1361926395241" MODIFIED="1361926446520"/>
</node>
<node TEXT="Update documentation of DSL language" ID="ID_1591678873" CREATED="1361329761023" MODIFIED="1361342828839" BACKGROUND_COLOR="#ff9c9c">
<node TEXT="Automatic param validation" ID="ID_208594967" CREATED="1361926206091" MODIFIED="1361926218473"/>
</node>
<node TEXT="How to run regression tests." ID="ID_203979375" CREATED="1361330527498" MODIFIED="1361342828844" BACKGROUND_COLOR="#ff9c9c"/>
<node TEXT="Running the Logging Proxy" ID="ID_1167519617" CREATED="1361926222386" MODIFIED="1361926244531"/>
<node TEXT="Running the LoadTool" ID="ID_423703853" CREATED="1361926245169" MODIFIED="1361926289941"/>
</node>
<node TEXT="Testing Admin Tool And DSL" POSITION="left" ID="ID_1850217554" CREATED="1361329344880" MODIFIED="1361329357992">
<node TEXT="Unit Tests For FlowTestDSL" ID="ID_775525918" CREATED="1361329511105" MODIFIED="1361329542868"/>
<node TEXT="Test harness for testing DSL scripts e.g. mocking calls to server and simulating responses." ID="ID_1464996551" CREATED="1361329549896" MODIFIED="1361342864931" BACKGROUND_COLOR="#ff9c9c"/>
<node TEXT="Should Print Server Response on Error" ID="ID_425100561" CREATED="1362101783193" MODIFIED="1363136824281">
<icon BUILTIN="button_ok"/>
<node TEXT="Response Handling" ID="ID_1317043521" CREATED="1362102324797" MODIFIED="1363136827015">
<icon BUILTIN="button_ok"/>
<node TEXT="Print out the returned message and the associated explanation from Kostya&apos;s Readme.md" ID="ID_1482239241" CREATED="1362102348927" MODIFIED="1363136830750">
<icon BUILTIN="button_ok"/>
</node>
</node>
</node>
<node TEXT="If you miss the protocol it should default to http." ID="ID_1029895252" CREATED="1362101813764" MODIFIED="1362101837129"/>
<node TEXT="If you get a parameter wrong it should print the usage." ID="ID_542250780" CREATED="1362101843908" MODIFIED="1363137007265">
<icon BUILTIN="button_ok"/>
</node>
</node>
<node TEXT="Monitoring Server" POSITION="left" ID="ID_1941888742" CREATED="1361329447976" MODIFIED="1361329454933">
<node TEXT="Monitoring Proxy" ID="ID_1299937294" CREATED="1361329608097" MODIFIED="1361329615824">
<node TEXT="Logging requests to a NoSQL database" ID="ID_60258611" CREATED="1361329628891" MODIFIED="1361342912772" BACKGROUND_COLOR="#ff9c9c"/>
<node TEXT="Generating playback scripts" ID="ID_1629894131" CREATED="1361329648701" MODIFIED="1361342912812" BACKGROUND_COLOR="#ff9c9c"/>
<node TEXT="flexible querying of request logs" ID="ID_1281150178" CREATED="1361330480911" MODIFIED="1361330514530"/>
<node TEXT="simulate slow server." ID="ID_671842087" CREATED="1361943720031" MODIFIED="1361943730908">
<node TEXT="Add in configurable delay before forwarding request" ID="ID_3801458" CREATED="1361943756545" MODIFIED="1361943775305"/>
</node>
</node>
</node>
<node TEXT="Bug Fixes" POSITION="left" ID="ID_890202894" CREATED="1361329486157" MODIFIED="1361329492098">
<node TEXT="Set amplafi-flow-client issues list" ID="ID_124901727" CREATED="1361329823505" MODIFIED="1361329843591"/>
</node>
<node TEXT="Load Testing" POSITION="left" ID="ID_1604087441" CREATED="1361926454901" MODIFIED="1361926460909">
<node TEXT="Run some real test scripts from the load tool?" ID="ID_533643103" CREATED="1361926461921" MODIFIED="1361926517836"/>
<node TEXT="https://github.com/amplafi/amplafi-flow-client/wiki/Single-Site-Load-Testing-Plan" ID="ID_743723633" CREATED="1363136755953" MODIFIED="1363136773218"/>
</node>
<node TEXT="Remove use of external scripts: no .sh only ant targets" POSITION="right" ID="ID_1821842168" CREATED="1361912481545" MODIFIED="1361912499052"/>
</node>
</map>
