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
.[[UsingExternalReactLibraries]]<<UsingExternalReactLibraries,Using External React Libraries>>
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




class TeXPreprocessor < Extensions::Preprocessor

  def process document, reader
    return reader if reader.eof?
    replacement_lines = reader.read_lines.map do |line|
      if(line.include? '$example$')
        parameters = line.split("$")[2]
        example_name = parameters.split(",")[0]
        example_id = parameters.split(",")[1]
        example_source =  parameters.split(",")[2]
        fullform = create_asciidoc_block(example_name, example_id, example_source)
        puts fullform
        (line.gsub line, fullform)
      else line
      end
    end
    reader.unshift_lines replacement_lines
    reader
  end
end
