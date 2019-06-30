# A block macro that embeds a Fulcro example into the output document
#
# Usage
#
#   $example$ "example_name", "example_id", "example_source"]
#   $example$ "victory_name", "victory_id", "./src/victory_example.cljs"]
##############
#   This gets converted into
##############
#
# .[[UsingExternalReactLibraries]]<<UsingExternalReactLibraries,Using External React Libraries>>
#   ====
#   ++++
#   <button class="inspector" onClick="book.main.focus('victory-example')">Focus Inspector</button>
#   <div class="short narrow example" id="victory-example"></div>
#   <br/>
#   ++++
#   [source,clojure,role="source"]
#   ----
#   include::src/book/book/ui/victory_example.cljs[]
#   ----
#   ====
#


require 'asciidoctor/extensions'

include Asciidoctor




def create_asciidoc_block example_name, example_id, example_source
  return %{
.[[SampleExample]]<<SampleExample,Sample Example>>
====
++++
<button class="inspector" onClick="book.main.focus('#{example_name}')">Focus Inspector</button>
<div class="short narrow example" id="#{example_id}"></div>
<br/>
++++
[source,clojure,role="source"]
----
include::#{example_source}[]
----
====
}
end




class FulcroPreprocessor < Extensions::Preprocessor
  def process document, reader
    return reader if reader.eof?

    replacement_lines = reader.read_lines.map do |line|
      if(line.include? '$example$')

        parameters = line.split("$")[2]
        example_name = parameters.split(",")[0].strip()
        example_id = parameters.split(",")[1].strip()
        example_source =  parameters.split(",")[2].strip()
        example_block = create_asciidoc_block(example_name, example_id, example_source)

        (line.gsub line, example_block)

      else
        line
      end
    end
    # TODO
    reader.unshift_all(replacement_lines)
    reader
    # NOTE Dumping all the lines from the reader to the console.
    #puts reader.lines
    File.open("ExpandedAsciiDocSource.adoc", "w") { |file| file.puts reader.lines}
  end
end
