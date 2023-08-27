# AWS::ResilienceHub::App PermissionModel

Defines the roles and credentials that AWS Resilience Hub would use while creating the application, importing its resources, and running an assessment.

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "<a href="#type" title="Type">Type</a>" : <i>String</i>,
    "<a href="#invokerrolename" title="InvokerRoleName">InvokerRoleName</a>" : <i>String</i>,
    "<a href="#crossaccountrolearns" title="CrossAccountRoleArns">CrossAccountRoleArns</a>" : <i>[ String, ... ]</i>
}
</pre>

### YAML

<pre>
<a href="#type" title="Type">Type</a>: <i>String</i>
<a href="#invokerrolename" title="InvokerRoleName">InvokerRoleName</a>: <i>String</i>
<a href="#crossaccountrolearns" title="CrossAccountRoleArns">CrossAccountRoleArns</a>: <i>
      - String</i>
</pre>

## Properties

#### Type

Defines how AWS Resilience Hub scans your resources. It can scan for the resources by using a pre-existing role in your AWS account, or by using the credentials of the current IAM user.

_Required_: Yes

_Type_: String

_Allowed Values_: <code>LegacyIAMUser</code> | <code>RoleBased</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### InvokerRoleName

Existing AWS IAM role name in the primary AWS account that will be assumed by AWS Resilience Hub Service Principle to obtain a read-only access to your application resources while running an assessment.

_Required_: No

_Type_: String

_Pattern_: <code>((\u002F[\u0021-\u007E]+\u002F){1,511})?[A-Za-z0-9+=,.@_/-]{1,64}</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### CrossAccountRoleArns

Defines a list of role Amazon Resource Names (ARNs) to be used in other accounts. These ARNs are used for querying purposes while importing resources and assessing your application.

_Required_: No

_Type_: List of String

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)
