# AWS::ResilienceHub::App ResourceMapping

Resource mapping is used to map logical resources from template to physical resource

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "<a href="#logicalstackname" title="LogicalStackName">LogicalStackName</a>" : <i>String</i>,
    "<a href="#mappingtype" title="MappingType">MappingType</a>" : <i>String</i>,
    "<a href="#resourcename" title="ResourceName">ResourceName</a>" : <i>String</i>,
    "<a href="#physicalresourceid" title="PhysicalResourceId">PhysicalResourceId</a>" : <i><a href="physicalresourceid.md">PhysicalResourceId</a></i>
}
</pre>

### YAML

<pre>
<a href="#logicalstackname" title="LogicalStackName">LogicalStackName</a>: <i>String</i>
<a href="#mappingtype" title="MappingType">MappingType</a>: <i>String</i>
<a href="#resourcename" title="ResourceName">ResourceName</a>: <i>String</i>
<a href="#physicalresourceid" title="PhysicalResourceId">PhysicalResourceId</a>: <i><a href="physicalresourceid.md">PhysicalResourceId</a></i>
</pre>

## Properties

#### LogicalStackName

_Required_: No

_Type_: String

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### MappingType

_Required_: Yes

_Type_: String

_Pattern_: <code>CfnStack|Resource</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### ResourceName

_Required_: No

_Type_: String

_Pattern_: <code>^[A-Za-z0-9][A-Za-z0-9_\-]{1,59}$</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### PhysicalResourceId

_Required_: Yes

_Type_: <a href="physicalresourceid.md">PhysicalResourceId</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)
