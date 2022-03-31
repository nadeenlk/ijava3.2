import java.util.Arrays;
import java.util.List;

class BuiltinClassLoader extends iClassLoader {
    Scope scope;
    static List<String> builtins;
    static {
        //https://docs.oracle.com/en/java/javase/15/docs/api/allpackages-index.html
        //[...document.querySelectorAll('.col-first')].slice(1).map(x=>x.innerText).toString()
        String s = "com.sun.jarsigner,com.sun.java.accessibility.util,com.sun.jdi,com.sun.jdi.connect,com.sun.jdi.connect.spi,com.sun.jdi.event,com.sun.jdi.request,com.sun.management,com.sun.net.httpserver,com.sun.net.httpserver.spi,com.sun.nio.sctp,com.sun.security.auth,com.sun.security.auth.callback,com.sun.security.auth.login,com.sun.security.auth.module,com.sun.security.jgss,com.sun.source.doctree,com.sun.source.tree,com.sun.source.util,com.sun.tools.attach,com.sun.tools.attach.spi,com.sun.tools.javac,com.sun.tools.jconsole,java.applet,java.awt,java.awt.color,java.awt.datatransfer,java.awt.desktop,java.awt.dnd,java.awt.event,java.awt.font,java.awt.geom,java.awt.im,java.awt.im.spi,java.awt.image,java.awt.image.renderable,java.awt.print,java.beans,java.beans.beancontext,java.io,java.lang,java.lang.annotation,java.lang.constant,java.lang.instrument,java.lang.invoke,java.lang.management,java.lang.module,java.lang.ref,java.lang.reflect,java.lang.runtime,java.math,java.net,java.net.http,java.net.spi,java.nio,java.nio.channels,java.nio.channels.spi,java.nio.charset,java.nio.charset.spi,java.nio.file,java.nio.file.attribute,java.nio.file.spi,java.rmi,java.rmi.activation,java.rmi.dgc,java.rmi.registry,java.rmi.server,java.security,java.security.cert,java.security.interfaces,java.security.spec,java.sql,java.text,java.text.spi,java.time,java.time.chrono,java.time.format,java.time.temporal,java.time.zone,java.util,java.util.concurrent,java.util.concurrent.atomic,java.util.concurrent.locks,java.util.function,java.util.jar,java.util.logging,java.util.prefs,java.util.regex,java.util.spi,java.util.stream,java.util.zip,javax.accessibility,javax.annotation.processing,javax.crypto,javax.crypto.interfaces,javax.crypto.spec,javax.imageio,javax.imageio.event,javax.imageio.metadata,javax.imageio.plugins.bmp,javax.imageio.plugins.jpeg,javax.imageio.plugins.tiff,javax.imageio.spi,javax.imageio.stream,javax.lang.model,javax.lang.model.element,javax.lang.model.type,javax.lang.model.util,javax.management,javax.management.loading,javax.management.modelmbean,javax.management.monitor,javax.management.openmbean,javax.management.relation,javax.management.remote,javax.management.remote.rmi,javax.management.timer,javax.naming,javax.naming.directory,javax.naming.event,javax.naming.ldap,javax.naming.ldap.spi,javax.naming.spi,javax.net,javax.net.ssl,javax.print,javax.print.attribute,javax.print.attribute.standard,javax.print.event,javax.rmi.ssl,javax.script,javax.security.auth,javax.security.auth.callback,javax.security.auth.kerberos,javax.security.auth.login,javax.security.auth.spi,javax.security.auth.x500,javax.security.cert,javax.security.sasl,javax.smartcardio,javax.sound.midi,javax.sound.midi.spi,javax.sound.sampled,javax.sound.sampled.spi,javax.sql,javax.sql.rowset,javax.sql.rowset.serial,javax.sql.rowset.spi,javax.swing,javax.swing.border,javax.swing.colorchooser,javax.swing.event,javax.swing.filechooser,javax.swing.plaf,javax.swing.plaf.basic,javax.swing.plaf.metal,javax.swing.plaf.multi,javax.swing.plaf.nimbus,javax.swing.plaf.synth,javax.swing.table,javax.swing.text,javax.swing.text.html,javax.swing.text.html.parser,javax.swing.text.rtf,javax.swing.tree,javax.swing.undo,javax.tools,javax.transaction.xa,javax.xml,javax.xml.catalog,javax.xml.crypto,javax.xml.crypto.dom,javax.xml.crypto.dsig,javax.xml.crypto.dsig.dom,javax.xml.crypto.dsig.keyinfo,javax.xml.crypto.dsig.spec,javax.xml.datatype,javax.xml.namespace,javax.xml.parsers,javax.xml.stream,javax.xml.stream.events,javax.xml.stream.util,javax.xml.transform,javax.xml.transform.dom,javax.xml.transform.sax,javax.xml.transform.stax,javax.xml.transform.stream,javax.xml.validation,javax.xml.xpath,jdk.dynalink,jdk.dynalink.beans,jdk.dynalink.linker,jdk.dynalink.linker.support,jdk.dynalink.support,jdk.incubator.foreign,jdk.javadoc.doclet,jdk.jfr,jdk.jfr.consumer,jdk.jshell,jdk.jshell.execution,jdk.jshell.spi,jdk.jshell.tool,jdk.management.jfr,jdk.net,jdk.nio,jdk.nio.mapmode,jdk.security.jarsigner,netscape.javascript,org.ietf.jgss,org.w3c.dom,org.w3c.dom.bootstrap,org.w3c.dom.css,org.w3c.dom.events,org.w3c.dom.html,org.w3c.dom.ls,org.w3c.dom.ranges,org.w3c.dom.stylesheets,org.w3c.dom.traversal,org.w3c.dom.views,org.w3c.dom.xpath,org.xml.sax,org.xml.sax.ext,org.xml.sax.helpers";
        builtins = Arrays.asList(s.split(","));
    }

    BuiltinClassLoader() {
        super();
        scope = ScopeImpl.newRootScope(null);
    }

    @Override
    iClass findClassImpl(String name) throws Throwable {
        if (builtins.stream().anyMatch(x -> name.startsWith(x)))
            return iClassWrapped.from(scope, Class.forName(name));
        throw new ClassNotFoundException(name);
    }

    @Override
    public String toString() {
        return "Builtin";
    }
}
